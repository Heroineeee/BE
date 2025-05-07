package com.kkinikong.be.store.dto;

import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.type.Category;

public record StoreDTO(
    Long id,
    String name,
    String address,
    double latitude,
    double longitude,
    Category category,
    double ratingAvg,
    long reviewCount,
    long scrapCount,
    long viewCount,
    Boolean isScrapped) {
  public static StoreDTO from(Store store) {
    return new StoreDTO(
        store.getId(),
        store.getName(),
        store.getAddress(),
        store.getLatitude(),
        store.getLongitude(),
        store.getCategory(),
        store.getRatingAvg(),
        store.getReviewCount(),
        store.getScrapCount(),
        store.getViewCount(),
        store.getIsScrapped());
  }
}
