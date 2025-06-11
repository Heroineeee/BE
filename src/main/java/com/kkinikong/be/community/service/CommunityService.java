package com.kkinikong.be.community.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.community.domain.Comment;
import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.domain.CommunityPostImage;
import com.kkinikong.be.community.dto.request.CommunityCommentRequest;
import com.kkinikong.be.community.dto.request.CommunityPostRequest;
import com.kkinikong.be.community.dto.response.CommunityPostResponse;
import com.kkinikong.be.community.exception.CommunityException;
import com.kkinikong.be.community.exception.errorcode.CommunityErrorCode;
import com.kkinikong.be.community.repository.CommentRepository;
import com.kkinikong.be.community.repository.CommunityPostImageRepository;
import com.kkinikong.be.community.repository.CommunityPostRepository;
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

  private final ImageService imageService;

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

    return new CommunityPostResponse(communityPost.getId());
  }

  @Transactional
  public void postCommunityPostImage(Long postId, List<MultipartFile> files, Long userId) {
    if (files == null || files.isEmpty()) return;
    if (files.size() > 3) {
      throw new CommunityException(CommunityErrorCode.COMMUNITY_POST_IMAGE_SIZE_LIMIT);
    }
    if (communityPostImageRepository.existsByCommunityPostId(postId)) {
      throw new CommunityException(CommunityErrorCode.COMMUNITY_POST_IMAGE_ALREADY_EXISTS);
    }

    CommunityPost communityPost = getCommunityPostOrThrow(postId);
    validatePostOwner(communityPost, userId);

    List<String> imageUrl = imageService.uploadFileList(files, S3Bucket.COMMUNITY_POST_IMAGE);

    for (String url : imageUrl) {
      communityPostImageRepository.save(
          CommunityPostImage.builder().communityPost(communityPost).imageUrl(url).build());
    }
  }

  @Transactional
  public void postCommentAndReply(
      Long postId, @Null Long commentId, CommunityCommentRequest request, Long userId) {
    CommunityPost communityPost = getCommunityPostOrThrow(postId);

    Comment parent = null;
    // 답글 작성 시
    if (commentId != null) {
      parent = getCommentOrThrow(commentId);
      // 댓글이 작성된 게시글과 일치하는지 확인
      if (!Objects.equals(parent.getCommunityPost().getId(), postId)) {
        throw new CommunityException(CommunityErrorCode.COMMUNITY_POST_NOT_FOUND);
      }
      // 답글의 답글은 허용하지 않음
      if (parent.getParentComment() != null) {
        throw new CommunityException(CommunityErrorCode.NOT_TOP_COMMENT);
      }
    }

    commentRepository.save(
        Comment.builder()
            .content(request.content())
            .communityPost(communityPost)
            .user(getUserOrThrow(userId))
            .parentComment(parent)
            .isAuthor(communityPost.getUser().getId().equals(userId))
            .build());

    communityPost.incrementCommentCount();
  }

  private void validatePostOwner(CommunityPost communityPost, Long userId) {
    if (!communityPost.getUser().getId().equals(userId)) {
      throw new CommunityException(CommunityErrorCode.COMMUNITY_NOT_OWNER);
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
