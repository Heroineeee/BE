package com.kkinikong.be.store.repository.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.type.Category;
import com.kkinikong.be.store.domain.type.StoreSort;

public interface StoreRepositoryCustom {

  Page<Store> findStoresUnified(
      Double latitude,
      Double longitude,
      Double radiusMeters,
      String keyword,
      Category category,
      StoreSort sort,
      Pageable pageable,
      Long userId);
}
