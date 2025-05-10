package com.kkinikong.be.store.repository.storetag;

import java.util.Optional;

import com.kkinikong.be.review.domain.type.Tag;

public interface StoreTagCountRepositoryCustom {
  Optional<Tag> findRepresentativeTagByStoreId(Long storeId);
}
