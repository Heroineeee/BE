package com.kkinikong.be.user.dto.response;

import com.kkinikong.be.store.domain.Store;

public record MypageStoreResponse(
    Long id,
    String name,
    String address,
    double latitude,
    double longitude,
    String category,
    double ratingAvg,
    long scrapCount) {
  public static MypageStoreResponse from(Store store) {
    return new MypageStoreResponse(
        store.getId(),
        store.getName(),
        store.getAddress(),
        store.getLatitude(),
        store.getLongitude(),
        store.getCategory().getLabel(),
        store.getRatingAvg(),
        store.getScrapCount());
  }
}
