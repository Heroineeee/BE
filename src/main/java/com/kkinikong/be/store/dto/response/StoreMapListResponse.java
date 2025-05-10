package com.kkinikong.be.store.dto.response;

import com.kkinikong.be.store.domain.Store;

public record StoreMapListResponse(
    Long id,
    String name,
    String address,
    double latitude,
    double longitude,
    String category,
    double ratingAvg,
    long scrapCount,
    Boolean isScrapped) {
  public static StoreMapListResponse from(Store store) {
    return new StoreMapListResponse(
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
