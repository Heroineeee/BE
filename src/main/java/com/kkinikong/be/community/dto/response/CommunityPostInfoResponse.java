package com.kkinikong.be.community.dto.response;

import java.util.List;

import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.util.TimeUtil;
import com.kkinikong.be.user.utils.UserNicknameUtil;

public record CommunityPostInfoResponse(
    Long communityPostId,
    String title,
    String nickname,
    String createdAt,
    long viewCount,
    boolean isModified,
    String category,
    String content,
    long likeCount,
    long commentCount,
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
        UserNicknameUtil.displayNickname(communityPost.getUser()),
        TimeUtil.relativeTimeFormatter(communityPost.getCreatedDate()),
        communityPost.getViewCount(),
        communityPost.isModified(),
        communityPost.getCategory().getLabel(),
        communityPost.getContent(),
        communityPost.getLikeCount(),
        communityPost.getCommentCount(),
        isLiked,
        isMyCommunityPost,
        commentListResponse);
  }
}
