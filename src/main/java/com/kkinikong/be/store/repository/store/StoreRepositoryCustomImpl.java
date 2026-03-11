package com.kkinikong.be.store.repository.store;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
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

  private static final double DEFAULT_RADIUS_METERS = 5000.0;
  private final JPAQueryFactory queryFactory;
  private final QStore store = QStore.store;
  private final QStoreScrap storeScrap = QStoreScrap.storeScrap;

  @Override
  public Page<Store> findStoresUnified(
      Double latitude,
      Double longitude,
      Double radiusMeters,
      String keyword,
      Category category,
      StoreSort sort,
      Pageable pageable,
      Long userId) {

    double radius = (radiusMeters != null) ? radiusMeters : DEFAULT_RADIUS_METERS;

    BooleanBuilder whereBuilder = new BooleanBuilder();
    whereBuilder.and(buildDistanceCondition(latitude, longitude, radius));

    if (category != null) {
      whereBuilder.and(store.category.eq(category));
    }

    if (keyword != null && !keyword.isBlank()) {
      whereBuilder.and(buildKeywordCondition(keyword));
    }

    OrderSpecifier<?>[] sortOrder = getSortOrder(sort, latitude, longitude);
    List<Tuple> tuples = fetchStores(whereBuilder, sortOrder, pageable, userId);
    List<Store> storeList = convertTuplesToStores(tuples, userId);
    long total = fetchTotalCount(whereBuilder);

    return new PageImpl<>(storeList, pageable, total);
  }

  @Override
  public List<Store> findTopViewedStores(Double latitude, Double longitude) {
    BooleanBuilder whereBuilder = buildDistanceCondition(latitude, longitude, 3000.0);
    OrderSpecifier<?>[] orderBy = getSortOrder(StoreSort.VIEW_COUNT, latitude, longitude);

    return queryFactory.selectFrom(store).where(whereBuilder).orderBy(orderBy).limit(8).fetch();
  }

  @Override
  public Page<Store> findStoresByIdsForMap(
      List<Long> ids, String keyword, Category category, Pageable pageable, Long userId) {

    BooleanBuilder whereBuilder = new BooleanBuilder();
    whereBuilder.and(store.id.in(ids)); // redis에서 필터링해준 id 리스트 안에 포함된 데이터만 조회

    if (category != null) {
      whereBuilder.and(store.category.eq(category));
    }

    if (keyword != null && !keyword.isBlank()) {
      whereBuilder.and(buildKeywordCondition(keyword));
    }

    String idsStr = String.join(", ", ids.stream().map(String::valueOf).toList());
    String format = "array_position(ARRAY[" + idsStr + "]::bigint[], {0})";
    OrderSpecifier<?> fieldOrder =
        new OrderSpecifier<>(
            com.querydsl.core.types.Order.ASC,
            Expressions.numberTemplate(Integer.class, format, store.id));
    OrderSpecifier<?>[] sortOrder = new OrderSpecifier[] {fieldOrder, store.id.asc()};

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

  private BooleanBuilder buildDistanceCondition(
      Double latitude, Double longitude, double radiusMeters) {

    BooleanBuilder builder = new BooleanBuilder();
    builder.and(
        Expressions.numberTemplate(
                Integer.class,
                "function('st_dwithin_meters', {0}, {1}, {2}, {3})",
                store.location,
                longitude,
                latitude,
                radiusMeters)
            .eq(1));

    return builder;
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
              "function('st_distance_meters', {0}, {1}, {2})",
              store.location,
              longitude.doubleValue(),
              latitude.doubleValue())
          .asc(),
      store.name.asc()
    };
  }
}
