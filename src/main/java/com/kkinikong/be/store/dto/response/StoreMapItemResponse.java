package com.kkinikong.be.store.dto.response;

import com.kkinikong.be.store.domain.Store;

public record StoreMapItemResponse(
    Long id,
    String name,
    String address,
    double latitude,
    double longitude,
    String category,
    double ratingAvg,
    long scrapCount,
    Boolean isScrapped) {
  public static StoreMapItemResponse from(Store store) {
    return new StoreMapItemResponse(
        store.getId(),
        store.getName(),
        store.getAddress(),
        store.getLatitude(),
        store.getLongitude(),
        store.getCategory().getLabel(),
        store.getRatingAvg(),
        store.getScrapCount(),
        store.getIsScrapped());
  }
}
