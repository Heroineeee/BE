package com.kkinikong.be.store.repository.store;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.store.domain.QStore;
import com.kkinikong.be.store.domain.QStoreScrap;
import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.type.Category;
import com.kkinikong.be.store.domain.type.StoreSort;

@RequiredArgsConstructor
public class StoreRepositoryCustomImpl implements StoreRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final QStore store = QStore.store;
  private final QStoreScrap storeScrap = QStoreScrap.storeScrap;

  @Override
  public Page<Store> findStoresForSorted(
      Double latitude,
      Double longitude,
      Category category,
      StoreSort sort,
      Pageable pageable,
      Long userId) {
    List<Tuple> tuples =
        queryFactory
            .select(store, storeScrap.id)
            .from(store)
            .leftJoin(storeScrap)
            .on(
                storeScrap
                    .store
                    .eq(store)
                    .and(userId != null ? storeScrap.user.id.eq(userId) : null))
            .where(category == null ? null : store.category.eq(category))
            .orderBy(getSortOrder(sort, latitude, longitude))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    List<Store> storeList =
        tuples.stream()
            .map(
                tuple -> {
                  Store s = tuple.get(store);
                  Long scrapId = tuple.get(storeScrap.id);
                  s.setIsScrapped(userId != null ? scrapId != null : null);
                  return s;
                })
            .toList();

    long total =
        queryFactory
            .select(store.count())
            .from(store)
            .where(category == null ? null : store.category.eq(category))
            .fetchOne();

    return new PageImpl<>(storeList, pageable, total);
  }

  @Override
  public Page<Store> findStoresByDistanceOrName(
      Double latitude, Double longitude, Category category, Pageable pageable, Long userId) {
    boolean useDistance = (latitude != null && longitude != null);
    OrderSpecifier<?>[] sortOrder =
        useDistance
            ? getDistanceOrder(latitude, longitude)
            : new OrderSpecifier[] {store.name.asc()};

    List<Tuple> tuples =
        queryFactory
            .select(store, storeScrap.id)
            .from(store)
            .leftJoin(storeScrap)
            .on(
                storeScrap
                    .store
                    .eq(store)
                    .and(userId != null ? storeScrap.user.id.eq(userId) : null))
            .where(category == null ? null : store.category.eq(category))
            .orderBy(sortOrder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    List<Store> storeList =
        tuples.stream()
            .map(
                tuple -> {
                  Store s = tuple.get(store);
                  Long scrapId = tuple.get(storeScrap.id);
                  s.setIsScrapped(userId != null ? scrapId != null : null);
                  return s;
                })
            .toList();

    long total =
        queryFactory
            .select(store.count())
            .from(store)
            .where(category == null ? null : store.category.eq(category))
            .fetchOne();

    return new PageImpl<>(storeList, pageable, total);
  }

  private OrderSpecifier<?>[] getSortOrder(StoreSort sort, Double latitude, Double longitude) {
    return switch (sort) {
      case DISTANCE -> getDistanceOrder(latitude, longitude);
      case RATING -> new OrderSpecifier[] {store.ratingAvg.desc().nullsLast(), store.name.asc()};
      case REVIEW_COUNT ->
          new OrderSpecifier[] {store.reviewCount.desc().nullsLast(), store.name.asc()};
      case VIEW_COUNT ->
          new OrderSpecifier[] {store.viewCount.desc().nullsLast(), store.name.asc()};
    };
  }

  private OrderSpecifier<?>[] getDistanceOrder(Double latitude, Double longitude) {
    return new OrderSpecifier[] {
      Expressions.numberTemplate(
              Double.class,
              "ST_Distance_Sphere(POINT({0}, {1}), POINT({2}, {3}))",
              store.longitude,
              store.latitude,
              longitude.doubleValue(),
              latitude.doubleValue())
          .asc(),
      store.name.asc()
    };
  }
}
