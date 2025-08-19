package com.kkinikong.be.convenience.dto.response;

import com.kkinikong.be.convenience.domain.ConveniencePost;
import com.kkinikong.be.global.util.TimeUtil;

public record ConveniencePostListResponse(
    Long id, String name, boolean isAvailable, String relativeCreatedAt) {
  public static ConveniencePostListResponse from(ConveniencePost conveniencePost) {
    return new ConveniencePostListResponse(
        conveniencePost.getId(),
        conveniencePost.getName(),
        conveniencePost.getIsAvailable(),
        TimeUtil.relativeTimeFormatter(conveniencePost.getCreatedDate()));
  }
}
