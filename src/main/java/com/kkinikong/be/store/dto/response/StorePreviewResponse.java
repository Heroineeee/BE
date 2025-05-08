package com.kkinikong.be.store.dto.response;

import com.kkinikong.be.store.domain.Store;

public record StorePreviewResponse(
    Long id,
    String name,
    String address,
    String category,
    double ratingAvg,
    long scrapCount,
    String representativeTag,
    Boolean isScrapped) {

  public static StorePreviewResponse from(Store store, String representativeTag) {
    return new StorePreviewResponse(
        store.getId(),
        store.getName(),
        store.getAddress(),
        store.getCategory().getLabel(),
        store.getRatingAvg(),
        store.getScrapCount(),
        representativeTag,
        store.getIsScrapped());
  }
}
