package com.kkinikong.be.store.dto.response;

import com.kkinikong.be.store.domain.Store;

public record StorePreviewResponse(
    Long id,
    String name,
    String address,
    double latitude,
    double longitude,
    String category,
    double ratingAvg,
    long reviewCount,
    long scrapCount,
    long viewCount) {

  public static StorePreviewResponse from(Store store) {
    return new StorePreviewResponse(
        store.getId(),
        store.getName(),
        store.getAddress(),
        store.getLatitude(),
        store.getLongitude(),
        store.getCategory().getLabel(),
        store.getRatingAvg(),
        store.getReviewCount(),
        store.getScrapCount(),
        store.getViewCount());
  }
}
