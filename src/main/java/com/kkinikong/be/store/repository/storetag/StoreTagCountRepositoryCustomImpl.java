package com.kkinikong.be.store.repository.storetag;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.review.domain.type.Tag;
import com.kkinikong.be.store.domain.QStoreTagCount;
import com.kkinikong.be.store.domain.StoreTagCount;

@Repository
@RequiredArgsConstructor
public class StoreTagCountRepositoryCustomImpl implements StoreTagCountRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<Tag> findRepresentativeTagByStoreId(Long storeId) {
    QStoreTagCount storeTagCount = QStoreTagCount.storeTagCount;

    return Optional.ofNullable(
        queryFactory
            .select(storeTagCount.tag)
            .from(storeTagCount)
            .where(storeTagCount.store.id.eq(storeId))
            .orderBy(storeTagCount.count.desc())
            .limit(1)
            .fetchOne());
  }

  @Override
  public Map<Long, Tag> findRepresentativeTagByStoreIdList(List<Long> storeIds) {
    QStoreTagCount storeTagCount = QStoreTagCount.toreTagCount;

    List<StoreTagCount> results =
        queryFactory
            .selectFrom(storeTagCount)
            .where(storeTagCount.store.id.in(storeIds))
            .orderBy(storeTagCount.store.id.asc(), storeTagCount.count.desc())
            .fetch();

    Map<Long, Tag> representativeTagMap = new LinkedHashMap<>();

    for (StoreTagCount tagCount : results) {
      Long storeId = tagCount.getStore().getId();
      if (!representativeTagMap.containsKey(storeId)) {
        representativeTagMap.put(storeId, tagCount.getTag());
      }
    }
    return representativeTagMap;
  }
}
