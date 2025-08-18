package com.kkinikong.be.community.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.cache.service.RedisTemplateCacheService;
import com.kkinikong.be.cache.type.RedisKey;
import com.kkinikong.be.community.domain.Comment;
import com.kkinikong.be.community.domain.CommentLike;
import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.domain.CommunityPostImage;
import com.kkinikong.be.community.domain.CommunityPostLike;
import com.kkinikong.be.community.domain.document.CommunityPostDocument;
import com.kkinikong.be.community.domain.type.Category;
import com.kkinikong.be.community.dto.request.CommunityCommentRequest;
import com.kkinikong.be.community.dto.request.CommunityPostRequest;
import com.kkinikong.be.community.dto.request.CommunityPostUpdateRequest;
import com.kkinikong.be.community.dto.response.CommentListResponse;
import com.kkinikong.be.community.dto.response.CommentResponse;
import com.kkinikong.be.community.dto.response.CommunityPostImageResponse;
import com.kkinikong.be.community.dto.response.CommunityPostInfoResponse;
import com.kkinikong.be.community.dto.response.CommunityPostListResponse;
import com.kkinikong.be.community.dto.response.CommunityPostPopularResponse;
import com.kkinikong.be.community.dto.response.CommunityPostPopularWrappingResponse;
import com.kkinikong.be.community.dto.response.CommunityPostResponse;
import com.kkinikong.be.community.dto.response.LikeToggleResponse;
import com.kkinikong.be.community.exception.CommunityException;
import com.kkinikong.be.community.exception.errorcode.CommunityErrorCode;
import com.kkinikong.be.community.repository.CommentLikeRepository;
import com.kkinikong.be.community.repository.CommunityPostImageRepository;
import com.kkinikong.be.community.repository.CommunityPostLikeRepository;
import com.kkinikong.be.community.repository.comment.CommentRepository;
import com.kkinikong.be.community.repository.communityPost.CommunityPostRepository;
import com.kkinikong.be.notification.event.payload.CommentLikeEvent;
import com.kkinikong.be.notification.event.payload.CommentReplyEvent;
import com.kkinikong.be.notification.event.payload.CommunityLikeEvent;
import com.kkinikong.be.opensearch.service.OpenSearchService;
import com.kkinikong.be.store.dto.response.StoreRecentSearchKeyword;
import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.exception.UserException;
import com.kkinikong.be.user.exception.errorcode.UserErrorCode;
import com.kkinikong.be.user.repository.UserRepository;
import com.kkinikong.be.util.s3.service.ImageService;
import com.kkinikong.be.util.s3.type.S3Bucket;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommunityService {

  private final CommunityPostRepository communityPostRepository;
  private final UserRepository userRepository;
  private final CommunityPostImageRepository communityPostImageRepository;
  private final CommentRepository commentRepository;
  private final CommunityPostLikeRepository communityPostLikeRepository;
  private final CommentLikeRepository commentLikeRepository;

  private final ApplicationEventPublisher eventPublisher;
  private final ImageService imageService;
  private final RedisTemplateCacheService redisTemplateCacheService;
  private final OpenSearchService openSearchService;

  final int maxRetries = 3;

  @Transactional
  public CommunityPostResponse postCommunityPost(CommunityPostRequest request, Long userId) {
    CommunityPost communityPost =
        communityPostRepository.save(
            CommunityPost.builder()
                .title(request.title())
                .content(request.content())
                .category(request.category())
                .user(getUserOrThrow(userId))
                .build());

    openSearchService.savePostToSearchIndex(CommunityPostDocument.from(communityPost));

    return new CommunityPostResponse(communityPost.getId());
  }

  @Transactional
  public CommunityPostImageResponse postCommunityPostImage(
      Long postId, List<MultipartFile> files, Long userId) {
    if (files == null || files.isEmpty()) return new CommunityPostImageResponse(List.of());

    List<CommunityPostImage> remainImageList =
        communityPostImageRepository.findAllByCommunityPostId(postId);
    if (remainImageList.size() + files.size() > 3) {
      throw new CommunityException(CommunityErrorCode.COMMUNITY_POST_IMAGE_SIZE_LIMIT);
    }

    CommunityPost communityPost = getCommunityPostOrThrow(postId);
    validatePostOwner(communityPost, userId);

    List<String> imageUrl = imageService.uploadFileList(files, S3Bucket.COMMUNITY_POST_IMAGE);

    for (String url : imageUrl) {
      communityPostImageRepository.save(
          CommunityPostImage.builder().communityPost(communityPost).imageUrl(url).build());
    }

    if (remainImageList.isEmpty()) {
      communityPost.updateThumbnailUrl(imageUrl.get(0));
    }

    return CommunityPostImageResponse.from(imageUrl);
  }

  @Transactional
  public CommentResponse postCommentAndReply(
      Long postId, Long commentId, CommunityCommentRequest request, Long userId) {
    CommunityPost communityPost = getCommunityPostOrThrow(postId);

    User sender = getUserOrThrow(userId);
    Comment parent = null;
    // 답글 작성인 경우
    if (commentId != null) {
      parent = validateReply(postId, commentId);
    }

    Comment comment =
        commentRepository.save(
            Comment.builder()
                .content(request.content())
                .communityPost(communityPost)
                .user(sender)
                .parentComment(parent)
                .isAuthor(communityPost.getUser().getId().equals(userId))
                .build());

    communityPost.incrementCommentCount();

    // 알림 이벤트 발행
    if (parent == null) {
      User receiver = communityPost.getUser();
      if (!communityPost.getUser().getId().equals(userId)) {
        eventPublisher.publishEvent(
            new CommentReplyEvent(receiver, sender, communityPost, comment));
      }
    } else {
      User receiver = parent.getUser();
      if (!parent.getUser().getId().equals(userId)) {
        eventPublisher.publishEvent(
            new CommentReplyEvent(receiver, sender, communityPost, comment));
      }
    }
    return CommentResponse.from(comment.getId());
  }

  @Cacheable(value = "community-popular-posts", unless = "#result == null")
  public CommunityPostPopularWrappingResponse getPopularCommunityPosts() {
    LocalDateTime since72Hours = LocalDateTime.now().minusHours(72);

    List<CommunityPostPopularResponse> list =
        communityPostRepository.findTop3ByLikesSince72Hours(since72Hours).stream()
            .map(CommunityPostPopularResponse::from)
            .toList();
    return new CommunityPostPopularWrappingResponse(List.copyOf(list));
  }

  public Page<CommunityPostListResponse> getCommunityPostList(
      Category category, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

    Page<CommunityPost> communityPosts;
    if (category == null) {
      communityPosts = communityPostRepository.findAll(pageable);
    } else {
      communityPosts = communityPostRepository.findAllByCategory(category, pageable);
    }
    return communityPosts.map(CommunityPostListResponse::from);
  }

  @Transactional
  public LikeToggleResponse postCommunityPostLike(Long postId, Long userId) {
    for (int attempt = 0; attempt < maxRetries; attempt++) {
      try {
        CommunityPost communityPost =
            communityPostRepository
                .findById(postId)
                .orElseThrow(
                    () -> new CommunityException(CommunityErrorCode.COMMUNITY_POST_NOT_FOUND));

        User user = getUserOrThrow(userId);

        Optional<CommunityPostLike> postLike =
            communityPostLikeRepository.findByCommunityPostIdAndUserId(postId, userId);

        boolean isLiked;
        if (postLike.isPresent()) {
          communityPostLikeRepository.delete(postLike.get());
          communityPost.decrementLikeCount();
          isLiked = false;
        } else {
          communityPostLikeRepository.save(
              CommunityPostLike.builder().communityPost(communityPost).user(user).build());
          communityPost.incrementLikeCount();
          isLiked = true;

          User receiver = communityPost.getUser();
          // 알림 이벤트 발행
          if (!communityPost.getUser().getId().equals(userId)) {
            eventPublisher.publishEvent(new CommunityLikeEvent(receiver, user, communityPost));
          }
        }

        // 버전 충돌 조기 감지를 위해 flush
        communityPostRepository.saveAndFlush(communityPost);
        return LikeToggleResponse.from(isLiked, communityPost.getLikeCount());
      } catch (ObjectOptimisticLockingFailureException e) {
        if (attempt == maxRetries - 1) {
          throw e;
        }
        try {
          Thread.sleep(30L * attempt);
        } catch (InterruptedException ignored) {
        }
      }
    }
    throw new CommunityException(CommunityErrorCode.COMMUNITY_POST_NOT_FOUND);
  }

  @Transactional
  public LikeToggleResponse postCommunityCommentLike(Long commentId, Long userId) {
    for (int attempt = 0; attempt < maxRetries; attempt++) {
      try {
        Comment comment =
            commentRepository
                .findById(commentId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMENT_NOT_FOUND));
        User user = getUserOrThrow(userId);

        Optional<CommentLike> commentLike =
            commentLikeRepository.findByCommentIdAndUserId(commentId, userId);

        boolean isLiked;
        if (commentLike.isPresent()) {
          commentLikeRepository.delete(commentLike.get());
          comment.decrementLikeCount();
          isLiked = false;
        } else {
          commentLikeRepository.save(CommentLike.builder().comment(comment).user(user).build());
          comment.incrementLikeCount();
          isLiked = true;

          User receiver = comment.getUser();
          // 알림 이벤트 발행
          if (!comment.getUser().getId().equals(userId)) {
            eventPublisher.publishEvent(new CommentLikeEvent(receiver, user, comment));
          }
        }

        // 버전 충돌 조기 감지를 위해 flush
        commentRepository.saveAndFlush(comment);
        return LikeToggleResponse.from(isLiked, comment.getLikeCount());
      } catch (ObjectOptimisticLockingFailureException e) {
        if (attempt == maxRetries - 1) {
          throw e;
        }
        try {
          Thread.sleep(30L * attempt);
        } catch (InterruptedException ignored) {
        }
      }
    }
    throw new CommunityException(CommunityErrorCode.COMMENT_NOT_FOUND);
  }

  public CommunityPostInfoResponse getCommunityPost(Long postId, Long userId) {
    CommunityPost communityPost = getCommunityPostOrThrow(postId);

    redisTemplateCacheService.increaseViewCounts(postId, RedisKey.COMMUNITY_POST_VIEWS_KEY);

    List<Comment> allComments = commentRepository.findAllByCommunityPostId(postId);
    List<String> allImages =
        communityPostImageRepository.findAllByCommunityPostId(postId).stream()
            .map(CommunityPostImage::getImageUrl)
            .toList();

    List<CommentListResponse> commentListResponses = mapToCommentTreeResponse(userId, allComments);

    return CommunityPostInfoResponse.from(
        communityPost,
        isUserLikedPost(userId, communityPost),
        isMyCommunityPost(userId, communityPost),
        allImages,
        commentListResponses);
  }

  public Page<CommunityPostListResponse> searchCommunityPost(
      String keyword, int page, int size, Long userId) {

    keyword = keyword.trim();
    // 최근 검색어 추가 로직
    if (userId != null) {
      redisTemplateCacheService.saveRecentSearch(userId, keyword);
    }

    // Elasticsearch에서 검색어로 커뮤니티 게시글 페이징해서 가져옴
    Pageable pageable = PageRequest.of(page, size);
    List<Long> communitySearchResponses =
        openSearchService.searchCommunityPost(keyword, page, size);

    if (communitySearchResponses.isEmpty()) {
      return new PageImpl<>(List.of(), pageable, 0);
    }

    Map<Long, CommunityPost> postMap =
        communityPostRepository.findByIdIn(communitySearchResponses).stream()
            .collect(Collectors.toMap(CommunityPost::getId, post -> post));

    // 순서 유지: 검색 결과에 있는 ID 순서대로 매핑
    List<CommunityPostListResponse> results =
        communitySearchResponses.stream()
            .map(
                id ->
                    Optional.ofNullable(postMap.get(id))
                        .map(CommunityPostListResponse::from)
                        .orElse(null))
            .filter(Objects::nonNull)
            .toList();

    return new PageImpl<>(results, pageable, communitySearchResponses.size());
  }

  public List<StoreRecentSearchKeyword> getRecentSearchKeywords(Long userId) {
    List<String> recentSearches = redisTemplateCacheService.getRecentSearches(userId);

    if (recentSearches.isEmpty()) {
      return List.of();
    }
    return recentSearches.stream().map(StoreRecentSearchKeyword::from).collect(Collectors.toList());
  }

  public void deleteRecentSearchKeyword(Long userId, String keyword) {
    keyword = keyword.trim();
    redisTemplateCacheService.deleteRecentSearches(userId, keyword);
  }

  @Transactional
  public void deleteCommunityPost(Long postId, Long userId) {
    CommunityPost communityPost = getCommunityPostOrThrow(postId);
    validatePostOwner(communityPost, userId);

    // 이미지 삭제
    communityPostImageRepository
        .findAllByCommunityPostId(postId)
        .forEach(
            image -> {
              imageService.deleteFile(image.getImageUrl(), S3Bucket.COMMUNITY_POST_IMAGE);
            });
    communityPostImageRepository.deleteAllByCommunityPostId(postId);

    // OpenSearch에서 게시글 삭제
    openSearchService.deletePostFromSearchIndex(postId);

    // orphan 관계로 댓글, 댓글 좋아요, 게시물 좋아요는 자동으로 삭제됨
    communityPostRepository.delete(communityPost);
  }

  @Transactional
  public void updateCommunityPost(Long postId, CommunityPostUpdateRequest request, Long userId) {
    CommunityPost communityPost = getCommunityPostOrThrow(postId);
    validatePostOwner(communityPost, userId);
    List<String> remaining =
        request.remainingImageUrls() == null
            ? Collections.emptyList()
            : request.remainingImageUrls();

    communityPostImageRepository
        .findAllByCommunityPostId(postId)
        .forEach(
            image -> {
              if (!remaining.contains(image.getImageUrl())) {
                imageService.deleteFile(image.getImageUrl(), S3Bucket.COMMUNITY_POST_IMAGE);
                communityPostImageRepository.deleteById(image.getId());
              }
            });

    communityPost.update(
        request.title(),
        request.content(),
        request.category(),
        remaining.isEmpty() ? null : remaining.get(0));

    openSearchService.deletePostFromSearchIndex(postId);
    openSearchService.savePostToSearchIndex(CommunityPostDocument.from(communityPost));
  }

  @Transactional
  public CommentResponse updateComment(
      Long commentId, CommunityCommentRequest request, Long userId) {
    Comment comment = getCommentOrThrow(commentId);
    validateCommentOwner(comment, userId);
    checkIsCommentDeleted(comment);

    comment.update(request.content());

    return CommentResponse.from(comment.getId());
  }

  @Transactional
  public void deleteComment(Long commentId, Long userId) {
    Comment comment = getCommentOrThrow(commentId);
    validateCommentOwner(comment, userId);
    checkIsCommentDeleted(comment);

    comment.updateIsDeleted();
  }

  private List<CommentListResponse> mapToCommentTreeResponse(
      Long userId, List<Comment> allComments) {

    // 부모 댓글을 찾고, 자식 댓글들을 그룹화하여 매핑
    Map<Long, List<Comment>> childrenMap =
        allComments.stream()
            .filter(comment -> comment.getParentComment() != null)
            .collect(Collectors.groupingBy(comment -> comment.getParentComment().getId()));

    // 부모 댓글만 필터링하여 리스트 생성
    List<Comment> parentComments =
        allComments.stream().filter(comment -> comment.getParentComment() == null).toList();

    return parentComments.stream()
        .map(
            parent -> {
              List<CommentListResponse> replyListResponse =
                  childrenMap.getOrDefault(parent.getId(), List.of()).stream()
                      .map(
                          child ->
                              CommentListResponse.from(
                                  child,
                                  isUserLikedComment(userId, child),
                                  isMyComment(userId, child),
                                  List.of()))
                      .toList();

              return CommentListResponse.from(
                  parent,
                  isUserLikedComment(userId, parent),
                  isMyComment(userId, parent),
                  replyListResponse);
            })
        .toList();
  }

  private Boolean isUserLikedComment(Long userId, Comment comment) {
    if (userId == null) {
      return null;
    }
    return comment.getCommentLikeList().stream()
        .anyMatch(commentLike -> commentLike.getUser().getId().equals(userId));
  }

  private Boolean isUserLikedPost(Long userId, CommunityPost communityPost) {
    if (userId == null) {
      return null;
    }
    return communityPost.getCommunityPostLikeList().stream()
        .anyMatch(like -> like.getUser().getId().equals(userId));
  }

  private Boolean isMyComment(Long userId, Comment comment) {
    if (userId == null) {
      return null;
    }
    return comment.getUser().getId().equals(userId);
  }

  private Boolean isMyCommunityPost(Long userId, CommunityPost communityPost) {
    if (userId == null) {
      return null;
    }
    return communityPost.getUser().getId().equals(userId);
  }

  private void checkIsCommentDeleted(Comment comment) {
    if (comment.isDeleted()) {
      throw new CommunityException(CommunityErrorCode.COMMENT_ALREADY_DELETED);
    }
  }

  private Comment validateReply(Long postId, Long commentId) {
    Comment parent = getCommentOrThrow(commentId);

    // 댓글이 작성된 게시글과 일치하는지 확인
    if (!Objects.equals(parent.getCommunityPost().getId(), postId)) {
      throw new CommunityException(CommunityErrorCode.COMMUNITY_POST_NOT_FOUND);
    }

    // 답글의 답글은 허용하지 않음
    if (parent.getParentComment() != null) {
      throw new CommunityException(CommunityErrorCode.NOT_TOP_COMMENT);
    }

    return parent;
  }

  private void validatePostOwner(CommunityPost communityPost, Long userId) {
    if (!communityPost.getUser().getId().equals(userId)) {
      throw new CommunityException(CommunityErrorCode.COMMUNITY_NOT_OWNER);
    }
  }

  private void validateCommentOwner(Comment comment, Long userId) {
    if (!comment.getUser().getId().equals(userId)) {
      throw new CommunityException(CommunityErrorCode.COMMENT_NOT_OWNER);
    }
  }

  private User getUserOrThrow(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
  }

  private CommunityPost getCommunityPostOrThrow(Long reviewId) {
    return communityPostRepository
        .findById(reviewId)
        .orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_POST_NOT_FOUND));
  }

  private Comment getCommentOrThrow(Long commentId) {
    return commentRepository
        .findById(commentId)
        .orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMENT_NOT_FOUND));
  }
}
