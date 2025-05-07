package com.kkinikong.be.store.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.dto.response.StoreInfoResponse;
import com.kkinikong.be.store.exception.StoreException;
import com.kkinikong.be.store.exception.errorcode.StoreErrorCode;
import com.kkinikong.be.store.repository.StoreRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

  private final StoreRepository storeRepository;

  public StoreInfoResponse getStoreInfo(Long storeId) {
    Store store =
        storeRepository
            .findStoreById(storeId)
            .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));

    return StoreInfoResponse.builder()
        .storeId(storeId)
        .storeCategory(store.getCategory().getLabel())
        .storeName(store.getName())
        .storeAddress(store.getAddress())
        .storeRating(store.getRatingAvg())
        .storeReviewCount(store.getReviewCount())
        .storeScrapCount(store.getScrapCount())
        .storeUpdatedDate(store.getUpdatedDate())
        .build();
  }
}
