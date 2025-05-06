package com.kkinikong.be.store.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.store.domain.QStore;
import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.type.Category;
import com.kkinikong.be.store.domain.type.StoreSort;

@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final QStore store = QStore.store;

  @Override
  public Page<Store> findStoresByFilterAndSort(
      double latitude, double longitude, Category category, StoreSort sort, Pageable pageable) {
    List<Store> stores =
        queryFactory
            .selectFrom(store)
            .where(category == null ? null : store.category.eq(category))
            .orderBy(getSortOrder(sort, latitude, longitude))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    long total =
        queryFactory
            .select(store.count())
            .from(store)
            .where(category == null ? null : store.category.eq(category))
            .fetchOne();

    return new PageImpl<>(stores, pageable, total);
  }

  private OrderSpecifier<?> getSortOrder(StoreSort sort, double latitude, double longitude) {
    switch (sort) {
      case DISTANCE -> {
        // 위도/경도 기반 거리 계산
        return Expressions.numberTemplate(
                Double.class,
                "ST_Distance_Sphere(POINT({0}, {1}), POINT({2}, {3}))",
                store.longitude,
                store.latitude,
                longitude,
                latitude)
            .asc();
      }
      case RATING -> {
        return store.ratingAvg.desc();
      }
      case REVIEW_COUNT -> {
        return store.reviewCount.desc();
      }
      case VIEW_COUNT -> {
        return store.viewCount.desc();
      }
      default -> {
        return store.name.asc(); // 가나다 순 정렬 (필터 결과 동일할 경우)
      }
    }
  }
}
