package com.kkinikong.be.review.repository.reviewtag;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.review.domain.QReview;
import com.kkinikong.be.review.domain.QReviewTag;
import com.kkinikong.be.review.domain.mapping.QReviewTagMap;

@RequiredArgsConstructor
public class ReviewTagRepositoryImpl implements ReviewTagRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Map<Long, String> findRepresentativeTagByStoreId(List<Long> storeIds) {
    QReview review = QReview.review;
    QReviewTag tag = QReviewTag.reviewTag;
    QReviewTagMap tagMap = QReviewTagMap.reviewTagMap;

    List<Tuple> result =
        jpaQueryFactory
            .select(review.store.id, tag.name, tagMap.count())
            .from(tagMap)
            .join(tagMap.review, review)
            .join(tagMap.reviewTag, tag)
            .where(review.store.id.in(storeIds))
            .groupBy(review.store.id, tag.name)
            .orderBy(review.store.id.asc(), tagMap.count().desc(), tag.name.asc())
            .fetch();

    Map<Long, String> storeIdToTag = new LinkedHashMap<>();
    for (Tuple tuple : result) {
      Long storeId = tuple.get(review.store.id);
      String tagName = tuple.get(tag.name);
      storeIdToTag.putIfAbsent(storeId, tagName);
    }
    return storeIdToTag;
  }
}
