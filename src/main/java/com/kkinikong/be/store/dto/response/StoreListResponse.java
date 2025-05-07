package com.kkinikong.be.store.dto.response;

import java.util.List;

import com.kkinikong.be.store.dto.StoreListDTO;

public record StoreListResponse(
    List<StorePreviewResponse> storesList, int totalPage, int currentPage) {

  public static StoreListResponse from(StoreListDTO storeListDTO) {
    return new StoreListResponse(
        storeListDTO.storeDTOList().stream().map(StorePreviewResponse::from).toList(),
        storeListDTO.totalPage(),
        storeListDTO.currentPage());
  }
}
