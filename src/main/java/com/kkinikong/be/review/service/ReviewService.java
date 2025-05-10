package com.kkinikong.be.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.review.domain.Review;
import com.kkinikong.be.review.domain.type.Tag;
import com.kkinikong.be.review.dto.request.ReviewRequest;
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

  @Transactional
  public void postReview(Long storeId, ReviewRequest request, Long userId) {
    Store store = getStoreOrThrow(storeId);

    Review.builder()
        .rating(request.rating())
        .content(request.content())
        .store(store)
        .user(getUserOrThrow(userId))
        .build();

    for (Tag tag : request.tag()) {
      if (storeTagCountRepository.existsByStoreIdAndTag(storeId, tag)) {
        storeTagCountRepository.findByStoreIdAndTag(storeId, tag).incrementCount();
      } else {
        storeTagCountRepository.save(StoreTagCount.builder().store(store).tag(tag).build());
      }
    }
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

  private StoreTagCount getStoreTagCount(Long storeId, Tag tag) {
    return storeTagCountRepository.findByStoreIdAndTag(storeId, tag);
  }
}
