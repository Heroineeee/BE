package com.kkinikong.be.store.dto.response;

import com.kkinikong.be.store.dto.StoreDTO;

public record StorePreviewResponse(
    Long id,
    String name,
    String address,
    String category,
    double ratingAvg,
    long scrapCount,
    String representativeTag,
    Boolean isScrapped) {

  public static StorePreviewResponse from(StoreDTO storeDTO) {
    return new StorePreviewResponse(
        storeDTO.id(),
        storeDTO.name(),
        storeDTO.address(),
        storeDTO.category().getLabel(),
        storeDTO.ratingAvg(),
        storeDTO.scrapCount(),
        storeDTO.representativeTag(),
        storeDTO.isScrapped());
  }
}
