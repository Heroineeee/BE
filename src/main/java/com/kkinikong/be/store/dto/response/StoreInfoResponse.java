package com.kkinikong.be.store.dto.response;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record StoreInfoResponse(
    Long storeId,
    String storeCategory,
    String storeName,
    String storeAddress,
    // Boolean isOpenNow,
    // List<String> weeklyOpenHours,
    Long storeScrapCount,
    LocalDate storeUpdatedDate,
    Long storeReviewCount,
    Double storeRating) {}
