package com.kkinikong.be.store.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;

import com.kkinikong.be.store.domain.type.Category;

@Builder
public record StoreInfoResponse(
    Long storeId,
    Category category,
    String storeName,
    String storeAddress,
    Boolean isOpenNow,
    List<String> weeklyOpenHours,
    Long storeScrapCount,
    LocalDate storeUpdatedDate,
    Long storeReviewCount,
    Double storeRating) {}
