package com.kkinikong.be.store.repository.storetag;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.kkinikong.be.review.domain.type.Tag;

public interface StoreTagCountRepositoryCustom {
  Optional<Tag> findRepresentativeTagByStoreId(Long storeId);

  Map<Long, Tag> findRepresentativeTagByStoreIdList(List<Long> storeIds);
}
