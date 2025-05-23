package com.kkinikong.be.store.dto.response;

import java.text.DecimalFormat;
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
    Double latitude,
    Double longitude,
    String representativeTag,
    String storeAddress,
    Map<String, List<String>> storeWeeklyOpeningHours,
    long storeScrapCount,
    LocalDate storeUpdatedDate,
    long storeReviewCount,
    String storeRating,
    Boolean isScrapped) {

  public static StoreInfoResponse from(
      Store store, String representativeTag, Map<String, List<String>> storeWeeklyOpeningHours) {
    return StoreInfoResponse.builder()
        .storeId(store.getId())
        .storeCategory(store.getCategory().getLabel())
        .storeName(store.getName())
        .latitude(store.getLatitude())
        .longitude(store.getLongitude())
        .representativeTag(representativeTag)
        .storeAddress(store.getAddress())
        .storeWeeklyOpeningHours(storeWeeklyOpeningHours)
        .storeScrapCount(store.getScrapCount())
        .storeUpdatedDate(store.getUpdatedDate())
        .storeReviewCount(store.getReviewCount())
        .storeRating(new DecimalFormat("#.##").format(store.getRatingAvg()))
        .isScrapped(store.getIsScrapped())
        .build();
  }
}
