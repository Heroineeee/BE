package com.kkinikong.be.user.dto.response;

import java.time.LocalDate;
import java.util.List;

import jakarta.annotation.Nullable;

import com.kkinikong.be.review.domain.Review;
import com.kkinikong.be.review.domain.type.Tag;

public record MypageReviewResponse(
    Long reviewId,
    LocalDate reviewDate,
    int rating,
    @Nullable List<String> tags,
    @Nullable String content,
    @Nullable String imageUrl) {
  public static MypageReviewResponse from(
      Review review, @Nullable List<Tag> tags, @Nullable String imageUrl) {
    return new MypageReviewResponse(
        review.getId(),
        review.getCreatedDate().toLocalDate(),
        review.getRating(),
        tags == null ? null : tags.stream().map(Tag::getLabel).toList(),
        review.getContent(),
        imageUrl);
  }
}
