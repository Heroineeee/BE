package com.kkinikong.be.user.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.community.domain.Comment;
import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.domain.CommunityPostLike;
import com.kkinikong.be.community.dto.response.CommunityPostListResponse;
import com.kkinikong.be.community.repository.CommunityPostLikeRepository;
import com.kkinikong.be.community.repository.comment.CommentRepository;
import com.kkinikong.be.community.repository.communityPost.CommunityPostRepository;
import com.kkinikong.be.convenience.domain.ConveniencePost;
import com.kkinikong.be.convenience.dto.response.ConveniencePostListResponse;
import com.kkinikong.be.convenience.repository.ConvenienceRepository;
import com.kkinikong.be.global.response.PageResponse;
import com.kkinikong.be.review.domain.Review;
import com.kkinikong.be.review.domain.type.Tag;
import com.kkinikong.be.review.repository.ReviewRepository;
import com.kkinikong.be.review.repository.reviewimage.ReviewImageRepository;
import com.kkinikong.be.review.repository.reviewtag.ReviewTagRepository;
import com.kkinikong.be.store.domain.StoreScrap;
import com.kkinikong.be.store.repository.storescrap.StoreScrapRepository;
import com.kkinikong.be.user.dto.response.MyCommentGroupByPostResponse;
import com.kkinikong.be.user.dto.response.MypageReviewResponse;
import com.kkinikong.be.user.dto.response.MypageStoreResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageService {
  private final StoreScrapRepository storeScrapRepository;
  private final ReviewRepository reviewRepository;
  private final ReviewImageRepository reviewImageRepository;
  private final ReviewTagRepository reviewTagRepository;
  private final ConvenienceRepository convenienceRepository;
  private final CommunityPostRepository communityPostRepository;
  private final CommunityPostLikeRepository communityPostLikeRepository;
  private final CommentRepository commentRepository;

  public List<MypageStoreResponse> getScrapStore(Long userId) {
    List<StoreScrap> scraps = storeScrapRepository.findAllByUserIdOrderByCreatedDateDesc(userId);
    return scraps.stream().map(scrap -> MypageStoreResponse.from(scrap.getStore())).toList();
  }

  public PageResponse<MypageReviewResponse> getReview(Long userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Review> reviewPage =
        reviewRepository.findAllByUserIdOrderByCreatedDateDesc(userId, pageable);

    Map<Long, String> reviewImageMap =
        reviewImageRepository.getReviewImageByReviewIds(reviewPage.map(Review::getId).getContent());
    Map<Long, List<Tag>> reviewTagMap =
        reviewTagRepository.getReviewTagsByReviewIds(reviewPage.map(Review::getId).getContent());

    return PageResponse.from(
        reviewPage,
        review ->
            MypageReviewResponse.from(
                review, reviewTagMap.get(review.getId()), reviewImageMap.get(review.getId())));
  }

  public PageResponse<ConveniencePostListResponse> getConveniencePost(
      Long userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<ConveniencePost> conveniencePostPage =
        convenienceRepository.findAllByUserIdOrderByCreatedDateDesc(userId, pageable);
    return PageResponse.from(conveniencePostPage, ConveniencePostListResponse::from);
  }

  public PageResponse<CommunityPostListResponse> getCommunityPost(Long userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<CommunityPost> communityPostPage =
        communityPostRepository.findAllByUserIdOrderByCreatedDateDesc(userId, pageable);
    return PageResponse.from(communityPostPage, CommunityPostListResponse::from);
  }

  public PageResponse<CommunityPostListResponse> getLikePost(Long userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
    Page<CommunityPostLike> communityPostListPage =
        communityPostLikeRepository.findAllByUserId(userId, pageable);
    return PageResponse.from(
        communityPostListPage, like -> CommunityPostListResponse.from(like.getCommunityPost()));
  }

  public PageResponse<MyCommentGroupByPostResponse> getCommentList(
      Long userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);

    Page<CommunityPost> postPage = commentRepository.findAllPostsWithMyComments(userId, pageable);
    List<Long> postIds = postPage.getContent().stream().map(CommunityPost::getId).toList();
    List<Comment> myComments = commentRepository.findMyCommentsInPosts(userId, postIds);

    Map<Long, List<Comment>> grouped =
        myComments.stream()
            .collect(Collectors.groupingBy(comment -> comment.getCommunityPost().getId()));

    List<MyCommentGroupByPostResponse> content =
        postPage.getContent().stream()
            .map(
                post ->
                    MyCommentGroupByPostResponse.from(
                        post, grouped.getOrDefault(post.getId(), List.of())))
            .toList();

    return PageResponse.of(
        content, postPage.getTotalPages(), postPage.getNumber(), postPage.getTotalElements());
  }
}
