package com.kkinikong.be.convenience.dto.response;

import com.kkinikong.be.convenience.domain.ConveniencePost;

public record ConveniencePostListResponse(Long id, String name, boolean isAvailable) {
  public static ConveniencePostListResponse from(ConveniencePost conveniencePost) {
    return new ConveniencePostListResponse(
        conveniencePost.getId(), conveniencePost.getName(), conveniencePost.getIsAvailable());
  }
}
