package com.kkinikong.be.review.dto.response;

import java.time.LocalDate;
import java.util.List;

import jakarta.annotation.Nullable;

public record ReviewItemResponse(
    String nickname,
    LocalDate reviewDate,
    int rating,
    List<String> tags,
    String content,
    @Nullable String imageUrl,
    Boolean isOwner) {
  public static ReviewItemResponse from(
      String nickname,
      LocalDate reviewDate,
      int rating,
      List<String> tags,
      String content,
      String imageUrl,
      Boolean isOwner) {
    return new ReviewItemResponse(nickname, reviewDate, rating, tags, content, imageUrl, isOwner);
  }
}
