package com.kkinikong.be.review.service;

import java.util.List;

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
import com.kkinikong.be.review.repository.ReviewImageRepository;
import com.kkinikong.be.review.repository.ReviewRepository;
import com.kkinikong.be.review.repository.ReviewTagRepository;
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

  private final ReviewImageService reviewImageService;

  @Transactional
  public ReviewPostResponse postReview(Long storeId, ReviewRequest request, Long userId) {
    // 리뷰 중복 작성 방지
    if (reviewRepository.existsReviewByUserIdAndStoreId(userId, storeId)) {
      throw new ReviewException(ReviewErrorCode.REVIEW_ALREADY_EXISTS);
    }

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
    updateTagCount(storeId, request.tag(), store);

    for (Tag tag : request.tag()) {
      reviewTagRepository.save(ReviewTag.builder().review(savedReview).tag(tag).build());
    }

    return new ReviewPostResponse(savedReview.getId());
  }

  @Transactional
  public void postReviewImage(Long reviewId, MultipartFile file, Long userId) {
    if (file == null || file.isEmpty()) {
      return;
    }

    Review review = getReviewOrThrow(reviewId);
    validateReviewOwner(review, userId);

    String imageUrl = reviewImageService.uploadFile(file);

    reviewImageRepository.save(ReviewImage.builder().review(review).imageUrl(imageUrl).build());
  }

  public ReviewListItemResponse getReviewListAndRating(
      Long storeId, int page, int size, Long userId) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

    Page<Review> reviewPage = reviewRepository.findReviewsByStoreId(storeId, pageable);

    PageResponse<ReviewItemResponse> pageResponse =
        PageResponse.from(
            reviewPage,
            review -> {
              String imageUrl =
                  reviewImageRepository
                      .findByReviewId(review.getId())
                      .map(ReviewImage::getImageUrl)
                      .orElse(null);

              User user = review.getUser();

              Boolean isOwner = null;
              if (userId != null) {
                isOwner = review.getUser().getId().equals(userId);
              }

              List<String> tags =
                  reviewTagRepository.findAllByReviewId(review.getId()).stream()
                      .map(ReviewTag::getTag)
                      .map(Tag::getLabel)
                      .toList();

              return ReviewItemResponse.from(
                  user.getNickname(),
                  review.getCreatedDate().toLocalDate(),
                  review.getRating(),
                  tags,
                  review.getContent(),
                  imageUrl,
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
              reviewImageService.deleteFile(image.getImageUrl());
              reviewImageRepository.delete(image);
            });

    updateReviewCountAndRatingAvgOnDelete(review.getRating(), store);

    // 추후 태그를 리뷰마다 저장한다면 여기서 태그 카운트도 줄여야 함
    // 현재는 리뷰 삭제 시 태그 카운트 줄이지 않음

    reviewRepository.delete(review);
  }

  private void updateTagCount(Long storeId, Tag[] tags, Store store) {
    if (tags == null) {
      return;
    }

    for (Tag tag : tags) {
      StoreTagCount tagCount =
          storeTagCountRepository
              .findByStoreIdAndTag(storeId, tag)
              .orElseGet(
                  () ->
                      storeTagCountRepository.save(
                          StoreTagCount.builder().store(store).tag(tag).build()));
      tagCount.incrementCount();
    }
  }

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
}
