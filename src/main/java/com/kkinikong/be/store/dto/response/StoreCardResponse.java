package com.kkinikong.be.store.dto.response;

import com.kkinikong.be.store.domain.Store;

public record StoreCardResponse(
    Long id, String name, String address, long viewCount, String category) {
  public static StoreCardResponse from(Store store) {
    return new StoreCardResponse(
        store.getId(),
        store.getName(),
        store.getAddress(),
        store.getViewCount(),
        store.getCategory().getLabel());
  }
}
