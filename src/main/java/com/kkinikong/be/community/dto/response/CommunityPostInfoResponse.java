package com.kkinikong.be.community.dto.response;

public record CommunityPostInfoResponse(
    Long communityPostId,
    String title,
    String nickname,
    String createdAt,
    long viewCount,
    boolean isModified,
    String category,
    String content,
    Long likeCount,
    Long commentCount,
    boolean isLiked,
    CommentListResponse commentListResponse) {}
