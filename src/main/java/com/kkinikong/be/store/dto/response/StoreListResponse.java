package com.kkinikong.be.store.dto.response;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.kkinikong.be.store.domain.Store;

public record StoreListResponse(
    List<StorePreviewResponse> storesList, int totalPage, int currentPage) {

  public static StoreListResponse from(Page<Store> storePage, Map<Long, String> tagMap) {
    return new StoreListResponse(
        storePage.getContent().stream()
            .map(store -> StorePreviewResponse.from(store, tagMap.get(store.getId())))
            .toList(),
        storePage.getTotalPages(),
        storePage.getNumber());
  }
}
