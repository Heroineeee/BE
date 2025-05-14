package com.kkinikong.be.store.repository.store;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.store.domain.QStore;
import com.kkinikong.be.store.domain.QStoreScrap;
import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.type.Category;
import com.kkinikong.be.store.domain.type.StoreSort;

@RequiredArgsConstructor
public class StoreRepositoryCustomImpl implements StoreRepositoryCustom {

  private static final double DEFAULT_RADIUS_METERS = 5000.0;

  private final JPAQueryFactory queryFactory;
  private final QStore store = QStore.store;
  private final QStoreScrap storeScrap = QStoreScrap.storeScrap;

  @Override
  public Page<Store> findStoresSorted(
      Double latitude,
      Double longitude,
      Category category,
      StoreSort sort,
      Pageable pageable,
      Long userId) {

    BooleanBuilder whereBuilder = new BooleanBuilder();
    whereBuilder.and(buildDistanceCondition(latitude, longitude, DEFAULT_RADIUS_METERS));

    if (category != null) {
      whereBuilder.and(store.category.eq(category));
    }

    List<Tuple> tuples =
        fetchStores(whereBuilder, getSortOrder(sort, latitude, longitude), pageable, userId);
    List<Store> storeList = convertTuplesToStores(tuples, userId);
    long total = fetchTotalCount(whereBuilder);

    return new PageImpl<>(storeList, pageable, total);
  }

  @Override
  public Page<Store> findStoresByNearest(
      Double latitude, Double longitude, Category category, Pageable pageable, Long userId) {

    BooleanBuilder whereBuilder = new BooleanBuilder();
    whereBuilder.and(buildDistanceCondition(latitude, longitude, DEFAULT_RADIUS_METERS));

    if (category != null) {
      whereBuilder.and(store.category.eq(category));
    }

    OrderSpecifier<?>[] sortOrder = getDistanceOrder(latitude, longitude);

    List<Tuple> tuples = fetchStores(whereBuilder, sortOrder, pageable, userId);
    List<Store> storeList = convertTuplesToStores(tuples, userId);
    long total = fetchTotalCount(whereBuilder);

    return new PageImpl<>(storeList, pageable, total);
  }

  @Override
  public Page<Store> searchStoresSorted(
      Double latitude,
      Double longitude,
      String keyword,
      StoreSort sort,
      Pageable pageable,
      Long userId) {

    BooleanBuilder whereBuilder = new BooleanBuilder();
    whereBuilder.and(buildDistanceCondition(latitude, longitude, DEFAULT_RADIUS_METERS));

    if (keyword != null && !keyword.isBlank()) {
      whereBuilder.and(buildKeywordCondition(keyword));
    }

    List<Tuple> tuples =
        fetchStores(whereBuilder, getSortOrder(sort, latitude, longitude), pageable, userId);
    List<Store> storeList = convertTuplesToStores(tuples, userId);
    long total = fetchTotalCount(whereBuilder);

    return new PageImpl<>(storeList, pageable, total);
  }

  @Override
  public Page<Store> searchStoresByNearest(
      Double latitude, Double longitude, String keyword, Pageable pageable, Long userId) {
    BooleanBuilder whereBuilder = new BooleanBuilder();
    whereBuilder.and(buildDistanceCondition(latitude, longitude, DEFAULT_RADIUS_METERS));

    if (keyword != null && !keyword.isBlank()) {
      whereBuilder.and(buildKeywordCondition(keyword));
    }

    OrderSpecifier<?>[] sortOrder = getDistanceOrder(latitude, longitude);

    List<Tuple> tuples = fetchStores(whereBuilder, sortOrder, pageable, userId);
    List<Store> storeList = convertTuplesToStores(tuples, userId);
    long total = fetchTotalCount(whereBuilder);

    return new PageImpl<>(storeList, pageable, total);
  }

  // 키워드 검색 조건 생성
  private BooleanBuilder buildKeywordCondition(String keyword) {
    String normalizedKeyword = keyword.replaceAll("\\s+", "");
    BooleanBuilder keywordCondition = new BooleanBuilder();
    keywordCondition.or(
        Expressions.stringTemplate("replace({0}, ' ', '')", store.name)
            .containsIgnoreCase(normalizedKeyword));
    keywordCondition.or(
        Expressions.stringTemplate("replace({0}, ' ', '')", store.address)
            .containsIgnoreCase(normalizedKeyword));
    return keywordCondition;
  }

  private List<Tuple> fetchStores(
      BooleanBuilder whereBuilder,
      OrderSpecifier<?>[] orderSpecifiers,
      Pageable pageable,
      Long userId) {
    return queryFactory
        .select(store, storeScrap.id)
        .from(store)
        .leftJoin(storeScrap)
        .on(storeScrap.store.eq(store).and(userId != null ? storeScrap.user.id.eq(userId) : null))
        .where(whereBuilder)
        .orderBy(orderSpecifiers)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
  }

  private List<Store> convertTuplesToStores(List<Tuple> tuples, Long userId) {
    return tuples.stream()
        .map(
            tuple -> {
              Store s = tuple.get(store);
              Long scrapId = tuple.get(storeScrap.id);
              s.setIsScrapped(userId != null ? scrapId != null : null);
              return s;
            })
        .toList();
  }

  private long fetchTotalCount(BooleanBuilder whereBuilder) {
    return queryFactory.select(store.count()).from(store).where(whereBuilder).fetchOne();
  }

  // 지정된 반경 내 거리 조건 생성
  private BooleanBuilder buildDistanceCondition(
      Double latitude, Double longitude, double radiusMeters) {
    NumberTemplate<Double> distanceExpression =
        Expressions.numberTemplate(
            Double.class,
            "ST_Distance_Sphere(POINT({0}, {1}), POINT({2}, {3}))",
            store.longitude,
            store.latitude,
            longitude,
            latitude);
    return new BooleanBuilder(distanceExpression.loe(radiusMeters));
  }

  // 정렬 조건 선택
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

  // 거리순 정렬
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
