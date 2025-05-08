package com.kkinikong.be.store.dto.response;

import com.kkinikong.be.store.domain.Store;

public record StoreMapPreviewResponse(
    Long id, String name, String address, String category, double ratingAvg, Boolean isScrapped) {
  public static StoreMapPreviewResponse from(Store store) {
    return new StoreMapPreviewResponse(
        store.getId(),
        store.getName(),
        store.getAddress(),
        store.getCategory().getLabel(),
        store.getRatingAvg(),
        store.getIsScrapped());
  }
}
