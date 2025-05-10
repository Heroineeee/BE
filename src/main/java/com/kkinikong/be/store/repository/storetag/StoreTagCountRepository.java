package com.kkinikong.be.store.repository.storetag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.review.domain.type.Tag;
import com.kkinikong.be.store.domain.StoreTagCount;

@Repository
public interface StoreTagCountRepository
    extends JpaRepository<StoreTagCount, Long>, StoreTagCountRepositoryCustom {

  StoreTagCount findByStoreIdAndTag(Long storeId, Tag tag);

  boolean existsByStoreIdAndTag(Long storeId, Tag tag);
}
