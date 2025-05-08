package com.kkinikong.be.store.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.review.repository.reviewtag.ReviewTagRepository;
import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.type.Category;
import com.kkinikong.be.store.domain.type.StoreSort;
import com.kkinikong.be.store.dto.response.PageResponse;
import com.kkinikong.be.store.dto.response.StoreListItemResponse;
import com.kkinikong.be.store.dto.response.StoreMapItemResponse;
import com.kkinikong.be.store.repository.store.StoreRepository;

@RequiredArgsConstructor
@Service
public class StoreService {

  private final StoreRepository storeRepository;
  private final ReviewTagRepository reviewTagRepository;

  @Transactional(readOnly = true)
  public PageResponse<StoreListItemResponse> getStoreListWithTag(
      Double latitude,
      Double longitude,
      Category category,
      StoreSort sort,
      int page,
      int size,
      Long userId) {
    Pageable pageable = PageRequest.of(page, size);

    Page<Store> storePage =
        storeRepository.findStoresForSorted(latitude, longitude, category, sort, pageable, userId);

    // 가맹점 ID 리스트 뽑아 태그 조회
    List<Long> storeIds = storePage.getContent().stream().map(Store::getId).toList();
    Map<Long, String> tagMap = reviewTagRepository.findRepresentativeTagByStoreId(storeIds);

    return PageResponse.from(
        storePage, store -> StoreListItemResponse.from(store, tagMap.get(store.getId())));
  }

  @Transactional(readOnly = true)
  public PageResponse<StoreMapItemResponse> getStoreListWithMap(
      Double latitude, Double longitude, Category category, int page, int size, Long userId) {

    Pageable pageable = PageRequest.of(page, size);

    Page<Store> storePage =
        storeRepository.findStoresByDistanceOrName(latitude, longitude, category, pageable, userId);

    return PageResponse.from(storePage, StoreMapItemResponse::from);
  }
}
