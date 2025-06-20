package com.kkinikong.be.store.repository.storescrap;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.StoreScrap;
import com.kkinikong.be.user.domain.User;

public interface StoreScrapRepository
    extends JpaRepository<StoreScrap, Long>, StoreScrapRepositoryCustom {
  List<StoreScrap> store(Store store);

  Page<StoreScrap> findAllByUserIdOrderByCreatedDateDesc(Long userId, Pageable pageable);

  void deleteAllByUser(User user);

  @Query("SELECT ss FROM StoreScrap ss JOIN FETCH ss.store WHERE ss.user = :user")
  List<StoreScrap> findAllByUser(User user);

  Optional<StoreScrap> findByStoreIdAndUserId(Long storeId, Long userId);

  boolean existsByStoreIdAndUserId(Long storeId, Long userId);
}
