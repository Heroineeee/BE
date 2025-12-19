package com.kkinikong.be.store.repository.storeupload;

import java.time.LocalDateTime;
import java.util.List;

import com.kkinikong.be.store.domain.Store;

public interface StoreJdbcRepository {
  void upsertStores(List<Store> stores);

  void deleteMissingStores(String region, LocalDateTime startTime);
}
