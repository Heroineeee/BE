package com.kkinikong.be.community.dto.response;

import java.util.List;

import lombok.Builder;

import com.kkinikong.be.community.domain.Comment;
import com.kkinikong.be.community.util.TimeUtil;
import com.kkinikong.be.user.utils.UserNicknameUtil;

@Builder
public record CommentListResponse(
    Long commentId,
    String content,
    String nickname,
    String createdAt,
    boolean isModified,
    boolean isDeleted,
    long likeCount,
    Boolean isLiked,
    boolean isAuthor,
    Boolean isMyComment,
    List<CommentListResponse> replyListResponse) {

  public static CommentListResponse from(
      Comment comment,
      Boolean isLiked,
      Boolean isMyComment,
      List<CommentListResponse> replyListResponse) {
    return CommentListResponse.builder()
        .commentId(comment.getId())
        .content(comment.getContent())
        .nickname(UserNicknameUtil.displayNickname(comment.getUser()))
        .createdAt(TimeUtil.relativeTimeFormatter(comment.getCreatedDate()))
        .isModified(comment.isModified())
        .isDeleted(comment.isDeleted())
        .likeCount(comment.getLikeCount())
        .isLiked(isLiked)
        .isAuthor(comment.isAuthor())
        .isMyComment(isMyComment)
        .replyListResponse(replyListResponse)
        .build();
  }
}
