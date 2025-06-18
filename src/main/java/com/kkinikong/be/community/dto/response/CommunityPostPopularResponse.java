package com.kkinikong.be.community.dto.response;

import com.kkinikong.be.community.domain.CommunityPost;

public record CommunityPostPopularResponse(long communityPostId, String title) {
  public static CommunityPostPopularResponse from(CommunityPost communityPost) {
    return new CommunityPostPopularResponse(communityPost.getId(), communityPost.getTitle());
  }
}
