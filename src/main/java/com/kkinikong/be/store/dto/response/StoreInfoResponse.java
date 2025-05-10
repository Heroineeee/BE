package com.kkinikong.be.store.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.Builder;

import com.kkinikong.be.store.domain.Store;

@Builder
public record StoreInfoResponse(
    Long storeId,
    String storeCategory,
    String storeName,
    String representativeTag,
    String storeAddress,
    Map<String, List<String>> storeWeeklyOpeningHours,
    Long storeScrapCount,
    LocalDate storeUpdatedDate,
    Long storeReviewCount,
    Double storeRating) {

  public static StoreInfoResponse from(
      Store store, String representativeTag, Map<String, List<String>> storeWeeklyOpeningHours) {
    return StoreInfoResponse.builder()
        .storeId(store.getId())
        .storeCategory(store.getCategory().getLabel())
        .storeName(store.getName())
        .representativeTag(representativeTag)
        .storeAddress(store.getAddress())
        .storeWeeklyOpeningHours(storeWeeklyOpeningHours)
        .storeScrapCount(store.getScrapCount())
        .storeUpdatedDate(store.getUpdatedDate())
        .storeReviewCount(store.getReviewCount())
        .storeRating(store.getRatingAvg())
        .build();
  }
}
