package com.kkinikong.be.review.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.global.response.PageResponse;
import com.kkinikong.be.review.domain.Review;
import com.kkinikong.be.review.domain.ReviewImage;
import com.kkinikong.be.review.domain.ReviewTag;
import com.kkinikong.be.review.domain.type.Tag;
import com.kkinikong.be.review.dto.request.ReviewRequest;
import com.kkinikong.be.review.dto.response.ReviewItemResponse;
import com.kkinikong.be.review.dto.response.ReviewListItemResponse;
import com.kkinikong.be.review.dto.response.ReviewPostResponse;
import com.kkinikong.be.review.exception.ReviewException;
import com.kkinikong.be.review.exception.errorcode.ReviewErrorCode;
import com.kkinikong.be.review.repository.ReviewRepository;
import com.kkinikong.be.review.repository.reviewimage.ReviewImageRepository;
import com.kkinikong.be.review.repository.reviewtag.ReviewTagRepository;
import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.StoreTagCount;
import com.kkinikong.be.store.exception.StoreException;
import com.kkinikong.be.store.exception.errorcode.StoreErrorCode;
import com.kkinikong.be.store.repository.store.StoreRepository;
import com.kkinikong.be.store.repository.storetag.StoreTagCountRepository;
import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.exception.UserException;
import com.kkinikong.be.user.exception.errorcode.UserErrorCode;
import com.kkinikong.be.user.repository.UserRepository;
import com.kkinikong.be.user.utils.UserNicknameUtil;
import com.kkinikong.be.util.s3.service.ImageService;
import com.kkinikong.be.util.s3.type.S3Bucket;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewService {

  private final UserRepository userRepository;
  private final StoreRepository storeRepository;
  private final StoreTagCountRepository storeTagCountRepository;
  private final ReviewRepository reviewRepository;
  private final ReviewImageRepository reviewImageRepository;
  private final ReviewTagRepository reviewTagRepository;

  private final ImageService imageService;

  @Transactional
  public ReviewPostResponse postReview(Long storeId, ReviewRequest request, Long userId) {
    Store store = getStoreOrThrow(storeId);
    // 리뷰 저장
    Review savedReview =
        reviewRepository.save(
            Review.builder()
                .rating(request.rating())
                .content(request.content())
                .store(store)
                .user(getUserOrThrow(userId))
                .build());

    updateReviewCountAndRatingAvg(request.rating(), store);

    if (request.tag() != null) {
      updateTagCount(storeId, request.tag(), store, false);
      for (Tag tag : request.tag()) {
        reviewTagRepository.save(ReviewTag.builder().review(savedReview).tag(tag).build());
      }
    }

    return new ReviewPostResponse(savedReview.getId());
  }

  @Transactional
  public void postReviewImage(Long reviewId, MultipartFile file, Long userId) {
    if (file == null || file.isEmpty()) return;

    if (reviewImageRepository.existsByReviewId(reviewId)) {
      throw new ReviewException(ReviewErrorCode.REVIEW_IMAGE_ALREADY_EXISTS);
    }

    Review review = getReviewOrThrow(reviewId);
    validateReviewOwner(review, userId);

    String imageUrl = imageService.uploadSingleFile(file, S3Bucket.STORE_REVIEW_IMAGE);

    reviewImageRepository.save(ReviewImage.builder().review(review).imageUrl(imageUrl).build());
  }

  public ReviewListItemResponse getReviewListAndRating(
      Long storeId, int page, int size, Long userId) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

    Page<Review> reviewPage = reviewRepository.findReviewsByStoreId(storeId, pageable);

    List<Long> reviewIds = reviewPage.getContent().stream().map(Review::getId).toList();

    // 리뷰 작성자 Map
    Map<Long, User> reviewUserByReviewIds =
        reviewPage.getContent().stream().collect(Collectors.toMap(Review::getId, Review::getUser));

    // 이미지 Map
    Map<Long, String> reviewImageByReviewIds =
        reviewImageRepository.getReviewImageByReviewIds(reviewIds);

    // 태그 Map
    Map<Long, List<Tag>> reviewTagsByReviewIds =
        reviewTagRepository.getReviewTagsByReviewIds(reviewIds);

    PageResponse<ReviewItemResponse> pageResponse =
        PageResponse.from(
            reviewPage,
            review -> {
              User user = review.getUser();

              Boolean isOwner = null;
              if (userId != null) {
                isOwner = review.getUser().getId().equals(userId);
              }

              return ReviewItemResponse.from(
                  UserNicknameUtil.displayNickname(user),
                  review,
                  reviewTagsByReviewIds.get(review.getId()),
                  reviewImageByReviewIds.get(review.getId()),
                  isOwner);
            });

    return ReviewListItemResponse.from(getStoreOrThrow(storeId), pageResponse);
  }

  @Transactional
  public void deleteReview(Long storeId, Long reviewId, Long userId) {
    Store store = getStoreOrThrow(storeId);
    Review review = getReviewOrThrow(reviewId);

    validateReviewOwner(review, userId);

    // 리뷰 이미지 삭제 (S3도)
    reviewImageRepository
        .findByReviewId(reviewId)
        .ifPresent(
            image -> {
              imageService.deleteFile(image.getImageUrl(), S3Bucket.STORE_REVIEW_IMAGE);
              reviewImageRepository.delete(image);
            });

    updateReviewCountAndRatingAvgOnDelete(review.getRating(), store);

    List<Tag> tags =
        reviewTagRepository.findAllByReviewId(reviewId).stream().map(ReviewTag::getTag).toList();

    if (!tags.isEmpty()) {
      updateTagCount(storeId, tags, store, true);
      reviewTagRepository.deleteByReviewId(reviewId);
    }

    reviewRepository.delete(review);
  }

  /**
   * 리뷰 작성, 삭제 시 태그 카운트 업데이트
   *
   * @param storeId
   * @param tags
   * @param store
   * @param isDelete
   */
  private void updateTagCount(Long storeId, List<Tag> tags, Store store, Boolean isDelete) {
    for (Tag tag : tags) {
      StoreTagCount tagCount =
          storeTagCountRepository
              .findByStoreIdAndTag(storeId, tag)
              .orElseGet(
                  () -> {
                    return storeTagCountRepository.save(
                        StoreTagCount.builder().store(store).tag(tag).build());
                  });

      if (isDelete) {
        tagCount.decrementCount();
      } else {
        tagCount.incrementCount();
      }
    }
  }

  /**
   * 리뷰 작성 시 리뷰 수와 평점 평균 업데이트
   *
   * @param rating
   * @param store
   */
  private static void updateReviewCountAndRatingAvg(int rating, Store store) {
    long currentReviewCount = store.getReviewCount();
    double currentAvg = store.getRatingAvg();
    double newAvg;

    if (currentReviewCount == 0) {
      newAvg = (long) rating; // 첫 리뷰거나 유일한 리뷰인 경우
    } else {
      newAvg = ((currentAvg * currentReviewCount) + (long) rating) / (currentReviewCount + 1);
    }

    store.increaseReviewCount();
    store.updateRatingAvg(newAvg);
  }

  /**
   * 리뷰 삭제 시 리뷰 수와 평점 평균 업데이트
   *
   * @param rating
   * @param store
   */
  private static void updateReviewCountAndRatingAvgOnDelete(int rating, Store store) {
    long currentReviewCount = store.getReviewCount();
    double currentAvg = store.getRatingAvg();

    if (currentReviewCount == 1) {
      store.updateRatingAvg(0.0);
    } else {
      store.updateRatingAvg(
          ((currentAvg * currentReviewCount) - (long) rating) / (currentReviewCount - 1));
    }

    store.decreaseReviewCount();
  }

  private Store getStoreOrThrow(Long storeId) {
    return storeRepository
        .findById(storeId)
        .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));
  }

  private User getUserOrThrow(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
  }

  private Review getReviewOrThrow(Long reviewId) {
    return reviewRepository
        .findById(reviewId)
        .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));
  }

  private void validateReviewOwner(Review review, Long userId) {
    if (!review.getUser().getId().equals(userId)) {
      throw new ReviewException(ReviewErrorCode.REVIEW_NOT_AUTHORIZED);
    }
  }

  //
}
