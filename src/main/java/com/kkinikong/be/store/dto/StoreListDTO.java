package com.kkinikong.be.store.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.kkinikong.be.store.domain.Store;

public record StoreListDTO(List<StoreDTO> storeDTOList, int totalPage, int currentPage) {
  public static StoreListDTO from(Page<Store> storePage) {
    return new StoreListDTO(
        storePage.getContent().stream()
            .map(store -> StoreDTO.from(store, null)) // 비로그인 경우 null
            .toList(),
        storePage.getTotalPages(),
        storePage.getNumber());
  }
}
