package com.kkinikong.be.community.dto.response;

public record LikeToggleResponse(boolean isLiked, Long likeCount) {
  public static LikeToggleResponse from(boolean isLiked, Long likeCount) {
    return new LikeToggleResponse(isLiked, likeCount);
  }
}
