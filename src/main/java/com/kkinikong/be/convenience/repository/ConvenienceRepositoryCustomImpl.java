package com.kkinikong.be.convenience.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.convenience.domain.ConveniencePost;
import com.kkinikong.be.convenience.domain.QConveniencePost;
import com.kkinikong.be.convenience.domain.type.Brand;
import com.kkinikong.be.convenience.domain.type.Category;

@RequiredArgsConstructor
public class ConvenienceRepositoryCustomImpl implements ConvenienceRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<ConveniencePost> findByCondition(
      String keyword, Brand brand, Category category, Boolean isAvailableCheck, Pageable pageable) {
    QConveniencePost conveniencePost = QConveniencePost.conveniencePost;

    List<BooleanExpression> conditions =
        buildConditions(keyword, brand, category, isAvailableCheck, conveniencePost);
    List<OrderSpecifier<?>> orderSpecifiers =
        buildOrderSpecifiers(keyword, isAvailableCheck, conveniencePost);

    List<ConveniencePost> conveniencePosts =
        fetchConveniencePost(pageable, conveniencePost, conditions, orderSpecifiers);
    Long totalCount = fetchTotalCount(conveniencePost, conditions);

    return new PageImpl<>(conveniencePosts, pageable, totalCount);
  }

  // WHERE 조건을 빌드
  private List<BooleanExpression> buildConditions(
      String keyword,
      Brand brand,
      Category category,
      Boolean isAvailableCheck,
      QConveniencePost conveniencePost) {
    List<BooleanExpression> conditions = new ArrayList<>();

    if (keyword != null && !keyword.isBlank()) {
      conditions.add(conveniencePost.name.containsIgnoreCase(keyword));
    }
    if (brand != null) {
      conditions.add(conveniencePost.brand.eq(brand));
    }
    if (category != null) {
      conditions.add(conveniencePost.category.eq(category));
    }
    if (isAvailableCheck) {
      conditions.add(conveniencePost.isAvailable.eq(true));
    }
    return conditions;
  }

  // ORDER BY 조건을 빌드 (키워드, isAvailableCheck)
  private List<OrderSpecifier<?>> buildOrderSpecifiers(
      String keyword, boolean isAvailableCheck, QConveniencePost conveniencePost) {
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

    if (keyword != null && !keyword.isBlank()) {
      orderSpecifiers.add(buildKeywordAccuracyOrder(keyword, conveniencePost));
      orderSpecifiers.add(conveniencePost.name.asc());
      if (isAvailableCheck) {
        orderSpecifiers.addAll(buildHelpfulAndDateOrder(conveniencePost));
      }
    } else if (isAvailableCheck) {
      orderSpecifiers.addAll(buildHelpfulAndDateOrder(conveniencePost));
    } else {
      orderSpecifiers.add(conveniencePost.createdDate.desc()); // 최신순
    }

    return orderSpecifiers;
  }

  // 키워드 정확도에 따른 정렬 조건을 빌드
  private OrderSpecifier<Integer> buildKeywordAccuracyOrder(
      String keyword, QConveniencePost conveniencePost) {
    return Expressions.numberTemplate(
            Integer.class,
            "CASE "
                + "WHEN {0} = {1} THEN 0 " // 정확히 일치하는 경우
                + "WHEN {0} LIKE {2} THEN 1 " // 포함되는 경우
                + "ELSE 2 END",
            conveniencePost.name,
            keyword,
            "%" + keyword + "%")
        .asc();
  }

  // 도움이 돼요 순 + 최신순으로 정렬하는 조건을 빌드
  private List<OrderSpecifier<?>> buildHelpfulAndDateOrder(QConveniencePost conveniencePost) {
    return List.of(conveniencePost.correctCount.desc(), conveniencePost.createdDate.desc());
  }

  // 데이터 조회
  private List<ConveniencePost> fetchConveniencePost(
      Pageable pageable,
      QConveniencePost conveniencePost,
      List<BooleanExpression> conditions,
      List<OrderSpecifier<?>> orderSpecifiers) {
    return queryFactory
        .selectFrom(conveniencePost)
        .where(conditions.toArray(new BooleanExpression[0]))
        .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
  }

  // ConveniencePost의 총 개수 조회
  private Long fetchTotalCount(
      QConveniencePost conveniencePost, List<BooleanExpression> conditions) {
    return queryFactory
        .select(conveniencePost.count())
        .from(conveniencePost)
        .where(conditions.toArray(new BooleanExpression[0]))
        .fetchOne();
  }
}
