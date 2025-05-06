package com.kkinikong.be.store.dto.response;

import java.util.List;

public record StoreListResponse(
    List<StorePreviewResponse> storesList, int totalPage, int currentPage) {

  public static StoreListResponse of(
      List<StorePreviewResponse> stores, int totalPage, int currentPage) {
    return new StoreListResponse(stores, totalPage, currentPage);
  }
}
