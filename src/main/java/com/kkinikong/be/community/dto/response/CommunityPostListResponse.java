package com.kkinikong.be.community.dto.response;

import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.util.TimeUtil;

public record CommunityPostListResponse(
    long communityPostId,
    String category,
    String titlePreview,
    String contentPreview,
    String thumbnailUrl,
    int imageCount,
    String createdAt,
    long commentCount,
    long likeCount,
    long viewCount,
    String nickname) {
  public static CommunityPostListResponse from(CommunityPost communityPost) {
    return new CommunityPostListResponse(
        communityPost.getId(),
        communityPost.getCategory().getLabel(),
        shortContent(communityPost.getTitle(), 25),
        shortContent(communityPost.getContent(), 50),
        communityPost.getThumbnailUrl(),
        communityPost.getCommunityPostImageList().size(),
        TimeUtil.relativeTimeFormatter(communityPost.getCreatedDate()),
        communityPost.getCommentCount(),
        communityPost.getLikeCount(),
        communityPost.getViewCount(),
        communityPost.getUser().getNickname());
  }

  private static String shortContent(String content, int maxLength) {
    return content.length() > maxLength ? content.substring(0, maxLength) + "..." : content;
  }
}
