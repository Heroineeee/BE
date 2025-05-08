package com.kkinikong.be.store.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.Builder;

@Builder
public record StoreInfoResponse(
    Long storeId,
    String storeCategory,
    String storeName,
    String storeAddress,
    Map<String, List<String>> storeWeeklyOpeningHours,
    Long storeScrapCount,
    LocalDate storeUpdatedDate,
    Long storeReviewCount,
    Double storeRating) {}
