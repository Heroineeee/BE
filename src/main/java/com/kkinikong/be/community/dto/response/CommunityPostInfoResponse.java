package com.kkinikong.be.community.dto.response;

import java.util.List;

import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.util.TimeUtil;

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
    List<CommentListResponse> commentListResponse) {

  public static CommunityPostInfoResponse from(
      CommunityPost communityPost, boolean isLiked, List<CommentListResponse> commentListResponse) {
    return new CommunityPostInfoResponse(
        communityPost.getId(),
        communityPost.getTitle(),
        communityPost.getUser().getNickname(),
        TimeUtil.relativeTimeFomatter(communityPost.getCreatedDate()),
        communityPost.getViewCount(),
        !(communityPost.getCreatedDate() == communityPost.getModifiedDate()),
        communityPost.getCategory().getLabel(),
        communityPost.getContent(),
        communityPost.getLikeCount(),
        communityPost.getCommentCount(),
        isLiked,
        commentListResponse);
  }
}
