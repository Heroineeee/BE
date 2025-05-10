package com.kkinikong.be.review.dto.response;

import java.time.LocalDate;

import jakarta.annotation.Nullable;

public record ReviewItemResponse(
    String nickname, LocalDate reviewDate, int rating, String content, @Nullable String imageUrl) {
  public static ReviewItemResponse from(
      String nickname, LocalDate reviewDate, int rating, String content, String imageUrl) {
    return new ReviewItemResponse(nickname, reviewDate, rating, content, imageUrl);
  }
}
