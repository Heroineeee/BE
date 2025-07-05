package com.kkinikong.be.community.dto.response;

import java.util.List;

import lombok.Builder;

import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.util.TimeUtil;
import com.kkinikong.be.user.utils.UserNicknameUtil;

@Builder
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
    long commentCount,
    Boolean isLiked,
    Boolean isMyCommunityPost,
    List<String> imageUrls,
    List<CommentListResponse> commentListResponse) {

  public static CommunityPostInfoResponse from(
      CommunityPost communityPost,
      Boolean isLiked,
      Boolean isMyCommunityPost,
      List<String> imageUrls,
      List<CommentListResponse> commentListResponse) {

    if (communityPost.getReportCount() >= 3) {
      return CommunityPostInfoResponse.builder()
          .communityPostId(communityPost.getId())
          .title(communityPost.getTitle())
          .nickname(UserNicknameUtil.checkReportNickname(communityPost.getUser()))
          .createdAt(TimeUtil.relativeTimeFormatter(communityPost.getCreatedDate()))
          .viewCount(communityPost.getViewCount())
          .isModified(communityPost.isModified())
          .category(communityPost.getCategory().getLabel())
          .content("신고된 글입니다.")
          .likeCount(null)
          .commentCount(communityPost.getCommentCount())
          .isLiked(null)
          .isMyCommunityPost(isMyCommunityPost)
          .imageUrls(null)
          .commentListResponse(commentListResponse)
          .build();
    }
    return CommunityPostInfoResponse.builder()
        .communityPostId(communityPost.getId())
        .title(communityPost.getTitle())
        .nickname(UserNicknameUtil.displayNickname(communityPost.getUser()))
        .createdAt(TimeUtil.relativeTimeFormatter(communityPost.getCreatedDate()))
        .viewCount(communityPost.getViewCount())
        .isModified(communityPost.isModified())
        .category(communityPost.getCategory().getLabel())
        .content(communityPost.getContent())
        .likeCount(communityPost.getLikeCount())
        .commentCount(communityPost.getCommentCount())
        .isLiked(isLiked)
        .isMyCommunityPost(isMyCommunityPost)
        .imageUrls(imageUrls)
        .commentListResponse(commentListResponse)
        .build();
  }
}
