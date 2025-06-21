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
    Boolean isLiked,
    Boolean isMyCommunityPost,
    List<CommentListResponse> commentListResponse) {

  public static CommunityPostInfoResponse from(
      CommunityPost communityPost,
      Boolean isLiked,
      Boolean isMyCommunityPost,
      List<CommentListResponse> commentListResponse) {
    return new CommunityPostInfoResponse(
        communityPost.getId(),
        communityPost.getTitle(),
        communityPost.getUser().getNickname(),
        TimeUtil.relativeTimeFormatter(communityPost.getCreatedDate()),
        communityPost.getViewCount(),
        !communityPost.getCreatedDate().equals(communityPost.getModifiedDate()),
        communityPost.getCategory().getLabel(),
        communityPost.getContent(),
        communityPost.getLikeCount(),
        communityPost.getCommentCount(),
        isLiked,
        isMyCommunityPost,
        commentListResponse);
  }
}
