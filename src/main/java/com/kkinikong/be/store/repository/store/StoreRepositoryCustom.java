package com.kkinikong.be.store.repository.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.type.Category;
import com.kkinikong.be.store.domain.type.StoreSort;

public interface StoreRepositoryCustom {

  Page<Store> findStoresSorted(
      Double latitude,
      Double longitude,
      Category category,
      StoreSort sort,
      Pageable pageable,
      Long userId);

  Page<Store> findStoresByNearest(
      Double latitude,
      Double longitude,
      double radiusMeters,
      Category category,
      Pageable pageable,
      Long userId);

  Page<Store> searchStoresSorted(
      Double latitude,
      Double longitude,
      String keyword,
      StoreSort sort,
      Pageable pageable,
      Long userId);

  Page<Store> searchStoresByNearest(
      Double latitude,
      Double longitude,
      double radiusMeters,
      String keyword,
      Pageable pageable,
      Long userId);
}
