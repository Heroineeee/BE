package com.kkinikong.be.store.dto.response;

import java.time.LocalDate;

import com.kkinikong.be.store.domain.type.Category;

public record StoreInfoResponse(
    Long storeId,
    Category category,
    String storeName,
    String storeAddress,
    Long storeScrapCount,
    LocalDate storeUpdatedDate,
    Long storeReviewCount,
    Double storeRating) {}
