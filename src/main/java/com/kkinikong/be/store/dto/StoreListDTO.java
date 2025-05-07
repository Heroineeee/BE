package com.kkinikong.be.store.dto;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.kkinikong.be.store.domain.Store;

public record StoreListDTO(List<StoreDTO> storeDTOList, int totalPage, int currentPage) {
  public static StoreListDTO from(Page<Store> storePage, Map<Long, String> tagMap) {
    return new StoreListDTO(
        storePage.getContent().stream()
            .map(store -> StoreDTO.from(store, tagMap.get(store.getId())))
            .toList(),
        storePage.getTotalPages(),
        storePage.getNumber());
  }
}
