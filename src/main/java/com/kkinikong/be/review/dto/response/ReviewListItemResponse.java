package com.kkinikong.be.review.dto.response;

import com.kkinikong.be.global.response.PageResponse;
import com.kkinikong.be.store.domain.Store;

public record ReviewListItemResponse(
    double ratingAvg, long reviewCount, PageResponse<ReviewItemResponse> pageResponse) {
  public static ReviewListItemResponse from(
      Store store, PageResponse<ReviewItemResponse> pageResponse) {
    return new ReviewListItemResponse(store.getRatingAvg(), store.getReviewCount(), pageResponse);
  }
}
