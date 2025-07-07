package com.kkinikong.be.community.dto.response;

import java.util.List;

public record CommunityPostImageResponse(List<String> imageUrls) {
  public static CommunityPostImageResponse from(List<String> imageUrls) {
    return new CommunityPostImageResponse(imageUrls);
  }
}
