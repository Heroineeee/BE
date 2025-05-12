package com.kkinikong.be.review.repository.reviewtag;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.review.domain.QReviewTag;
import com.kkinikong.be.review.domain.type.Tag;

@RequiredArgsConstructor
public class ReviewTagCustomRepositoryImpl implements ReviewTagCustomRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Map<Long, List<Tag>> getReviewTagsByReviewIds(List<Long> reviewIds) {

    QReviewTag reviewTag = QReviewTag.reviewTag;

    return queryFactory
        .select(reviewTag.review.id, reviewTag.tag)
        .from(reviewTag)
        .where(reviewTag.review.id.in(reviewIds))
        .fetch()
        .stream()
        .collect(
            Collectors.groupingBy(
                tuple -> tuple.get(reviewTag.review.id),
                Collectors.mapping(tuple -> tuple.get(reviewTag.tag), Collectors.toList())));
  }
}
