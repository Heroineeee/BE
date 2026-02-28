package com.kkinikong.be.store.dto.response;

public record StoreRegionCountResponse(Long count) {

  public static StoreRegionCountResponse from(Long count) {
    return new StoreRegionCountResponse(count);
  }
}
