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
  public Page<Store> findStoresByCategoryAndSort(
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

  private OrderSpecifier<?>[] getSortOrder(StoreSort sort, double latitude, double longitude) {
    switch (sort) {
      case DISTANCE -> {
        return new OrderSpecifier[] {
          Expressions.numberTemplate(
                  Double.class,
                  "ST_Distance_Sphere(POINT({0}, {1}), POINT({2}, {3}))",
                  store.longitude,
                  store.latitude,
                  longitude,
                  latitude)
              .asc(),
          store.name.asc()
        };
      }
      case RATING -> {
        return new OrderSpecifier[] {store.ratingAvg.desc().nullsLast(), store.name.asc()};
      }
      case REVIEW_COUNT -> {
        return new OrderSpecifier[] {store.reviewCount.desc().nullsLast(), store.name.asc()};
      }
      case VIEW_COUNT -> {
        return new OrderSpecifier[] {store.viewCount.desc().nullsLast(), store.name.asc()};
      }
      default -> {
        return new OrderSpecifier[] {store.name.asc()};
      }
    }
  }
}
