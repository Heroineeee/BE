package com.kkinikong.be.review.dto.response;

import java.time.LocalDate;
import java.util.List;

import jakarta.annotation.Nullable;

import com.kkinikong.be.review.domain.Review;
import com.kkinikong.be.review.domain.type.Tag;

public record ReviewItemResponse(
    Long reviewId,
    String nickname,
    LocalDate reviewDate,
    int rating,
    @Nullable List<String> tags,
    @Nullable String content,
    @Nullable String imageUrl,
    Boolean isOwner) {

  public static ReviewItemResponse from(
      String nickname,
      Review review,
      @Nullable List<Tag> tags,
      @Nullable String imageUrl,
      Boolean isOwner) {
    return new ReviewItemResponse(
        review.getId(),
        nickname,
        review.getCreatedDate().toLocalDate(),
        review.getRating(),
        tags == null ? null : tags.stream().map(Tag::getLabel).toList(),
        review.getContent(),
        imageUrl,
        isOwner);
  }
}
