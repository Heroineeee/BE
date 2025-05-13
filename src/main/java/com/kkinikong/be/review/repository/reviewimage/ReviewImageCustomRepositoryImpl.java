package com.kkinikong.be.review.repository.reviewimage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.review.domain.QReviewImage;

@RequiredArgsConstructor
public class ReviewImageCustomRepositoryImpl implements ReviewImageCustomRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Map<Long, String> getReviewImageByReviewIds(List<Long> reviewIds) {
    QReviewImage reviewImage = QReviewImage.reviewImage;

    return queryFactory
        .select(reviewImage.review.id, reviewImage.imageUrl)
        .from(reviewImage)
        .where(reviewImage.review.id.in(reviewIds))
        .fetch()
        .stream()
        .collect(
            Collectors.toMap(
                tuple -> tuple.get(reviewImage.review.id),
                tuple -> tuple.get(reviewImage.imageUrl)));
  }
}
