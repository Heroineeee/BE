package com.kkinikong.be.store.repository.storeupload;

import java.util.List;

import com.kkinikong.be.store.domain.Store;

public interface StoreJdbcRepository {
  void saveAllByJdbcTemplate(List<Store> stores);

  List<String> findExistingStoreKeys(List<String> keys);
}
