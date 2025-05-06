package com.kkinikong.be.store.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.type.Category;
import com.kkinikong.be.store.domain.type.StoreSort;
import com.kkinikong.be.store.dto.response.StoreListResponse;
import com.kkinikong.be.store.dto.response.StorePreviewResponse;
import com.kkinikong.be.store.repository.StoreRepository;

@RequiredArgsConstructor
@Service
public class StoreService {

  private final StoreRepository storeRepository;

  public StoreListResponse getStores(
      double latitude, double longitude, Category category, StoreSort sort, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Store> stores =
        storeRepository.findStoresByFilterAndSort(latitude, longitude, category, sort, pageable);

    List<StorePreviewResponse> previewList =
        stores.getContent().stream().map(StorePreviewResponse::from).toList();

    return StoreListResponse.of(previewList, stores.getTotalPages(), stores.getNumber());
  }
}
