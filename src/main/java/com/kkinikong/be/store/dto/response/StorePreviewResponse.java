package com.kkinikong.be.store.dto.response;

import com.kkinikong.be.store.dto.StoreDTO;

public record StorePreviewResponse(
    Long id,
    String name,
    String address,
    String category,
    double ratingAvg,
    long reviewCount,
    long scrapCount,
    long viewCount,
    Boolean isScrapped) {

  public static StorePreviewResponse from(StoreDTO storeDTO) {
    return new StorePreviewResponse(
        storeDTO.id(),
        storeDTO.name(),
        storeDTO.address(),
        storeDTO.category().getLabel(),
        storeDTO.ratingAvg(),
        storeDTO.reviewCount(),
        storeDTO.scrapCount(),
        storeDTO.viewCount(),
        storeDTO.isScrapped());
  }
}
