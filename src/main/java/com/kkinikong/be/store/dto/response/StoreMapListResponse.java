package com.kkinikong.be.store.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.kkinikong.be.store.domain.Store;

public record StoreMapListResponse(
    List<StoreMapPreviewResponse> storesList, int totalPage, int currentPage) {

  public static StoreMapListResponse from(Page<Store> storePage) {
    List<StoreMapPreviewResponse> storeList =
        storePage.getContent().stream().map(StoreMapPreviewResponse::from).toList();
    return new StoreMapListResponse(storeList, storePage.getTotalPages(), storePage.getNumber());
  }
}
