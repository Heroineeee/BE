package com.kkinikong.be.store.repository.storescrap;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.StoreScrap;

public interface StoreScrapRepository
    extends JpaRepository<StoreScrap, Long>, StoreScrapRepositoryCustom {
  List<StoreScrap> store(Store store);

  Optional<StoreScrap> findByStoreIdAndUserId(Long storeId, Long userId);

  boolean existsByStoreIdAndUserId(Long storeId, Long userId);
}
