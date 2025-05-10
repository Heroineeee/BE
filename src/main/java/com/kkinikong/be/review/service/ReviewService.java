package com.kkinikong.be.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.review.domain.Review;
import com.kkinikong.be.review.domain.type.Tag;
import com.kkinikong.be.review.dto.request.ReviewRequest;
import com.kkinikong.be.review.dto.response.ReviewPostResponse;
import com.kkinikong.be.review.exception.ReviewException;
import com.kkinikong.be.review.exception.errorcode.ReviewErrorCode;
import com.kkinikong.be.review.repository.review.ReviewRepository;
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

    return new ReviewPostResponse(savedReview.getId());
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
}
