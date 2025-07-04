package com.kkinikong.be.user.dto.response;

import java.util.List;

import jakarta.annotation.Nullable;

import com.kkinikong.be.community.util.TimeUtil;
import com.kkinikong.be.review.domain.Review;
import com.kkinikong.be.review.domain.type.Tag;

public record MypageReviewResponse(
    Long reviewId,
    String storeName,
    String createdAt,
    int rating,
    @Nullable List<String> tags,
    @Nullable String content,
    @Nullable String imageUrl) {
  public static MypageReviewResponse from(
      Review review, @Nullable List<Tag> tags, @Nullable String imageUrl) {
    return new MypageReviewResponse(
        review.getId(),
        review.getStore().getName(),
        TimeUtil.relativeTimeFormatter(review.getCreatedDate()),
        review.getRating(),
        tags == null ? null : tags.stream().map(Tag::getLabel).toList(),
        review.getContent(),
        imageUrl);
  }
}
