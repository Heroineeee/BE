package com.kkinikong.be.store.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.review.repository.reviewtag.ReviewTagRepository;
import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.type.Category;
import com.kkinikong.be.store.domain.type.StoreSort;
import com.kkinikong.be.store.dto.response.PagedResponse;
import com.kkinikong.be.store.dto.response.StoreMapPreviewResponse;
import com.kkinikong.be.store.dto.response.StorePreviewResponse;
import com.kkinikong.be.store.repository.store.StoreRepository;

@RequiredArgsConstructor
@Service
public class StoreService {

  private final StoreRepository storeRepository;
  private final ReviewTagRepository reviewTagRepository;

  public PagedResponse<StorePreviewResponse> getStores(
      Double latitude,
      Double longitude,
      Category category,
      StoreSort sort,
      int page,
      int size,
      Long userId) {
    Pageable pageable = PageRequest.of(page, size);

    // 가맹점 목록 조회
    Page<Store> storePage =
        storeRepository.findStoresByCategoryAndSort(
            latitude, longitude, category, sort, pageable, userId);

    // 가맹점 ID 리스트 뽑기
    List<Long> storeIds = storePage.getContent().stream().map(Store::getId).toList();

    // 대표 태그 조회
    Map<Long, String> tagMap = reviewTagRepository.findRepresentativeTagByStoreId(storeIds);

    return PagedResponse.from(
        storePage, store -> StorePreviewResponse.from(store, tagMap.get(store.getId())));
  }

  public PagedResponse<StoreMapPreviewResponse> getStoresForMap(
      Double latitude,
      Double longitude,
      Category category,
      StoreSort sort,
      int page,
      int size,
      Long userId) {

    Pageable pageable = PageRequest.of(page, size);

    Page<Store> storePage =
        storeRepository.findStoresByCategoryAndSort(
            latitude, longitude, category, sort, pageable, userId);

    return PagedResponse.from(storePage, StoreMapPreviewResponse::from);
  }
}
