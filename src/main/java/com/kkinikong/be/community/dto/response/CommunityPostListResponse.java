package com.kkinikong.be.community.dto.response;

public record CommunityPostListResponse(
    String communityPostId,
    String category,
    String titlePreview,
    String contentPreview,
    String thumbnailUrl,
    int imageCount,
    String createdAt,
    long commentCount,
    long likeCount,
    long viewCount
    // String nickname
    ) {}
