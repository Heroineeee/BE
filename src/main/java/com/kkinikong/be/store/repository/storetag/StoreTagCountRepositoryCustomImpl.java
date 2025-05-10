package com.kkinikong.be.store.repository.storetag;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.review.domain.type.Tag;
import com.kkinikong.be.store.domain.QStoreTagCount;

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
}
