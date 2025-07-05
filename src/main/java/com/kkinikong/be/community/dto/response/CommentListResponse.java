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
    Long likeCount,
    Boolean isLiked,
    boolean isAuthor,
    Boolean isMyComment,
    List<CommentListResponse> replyListResponse) {

  public static CommentListResponse from(
      Comment comment,
      Boolean isLiked,
      Boolean isMyComment,
      List<CommentListResponse> replyListResponse) {

    if (comment.getReportCount() >= 3) {
      return CommentListResponse.builder()
          .commentId(comment.getId())
          .content("신고된 댓글입니다")
          .nickname(UserNicknameUtil.checkReportNickname(comment.getUser()))
          .createdAt(TimeUtil.relativeTimeFormatter(comment.getCreatedDate()))
          .isModified(comment.isModified())
          .likeCount(null)
          .isLiked(null)
          .isAuthor(comment.isAuthor())
          .isMyComment(isMyComment)
          .replyListResponse(replyListResponse)
          .build();
    } else if (comment.isDeleted()) {
      return CommentListResponse.builder()
          .commentId(comment.getId())
          .content("삭제된 댓글입니다")
          .nickname(UserNicknameUtil.displayNickname(comment.getUser()))
          .createdAt(TimeUtil.relativeTimeFormatter(comment.getCreatedDate()))
          .isModified(comment.isModified())
          .likeCount(null)
          .isLiked(null)
          .isAuthor(comment.isAuthor())
          .isMyComment(isMyComment)
          .replyListResponse(replyListResponse)
          .build();
    }
    return CommentListResponse.builder()
        .commentId(comment.getId())
        .content(comment.getContent())
        .nickname(UserNicknameUtil.displayNickname(comment.getUser()))
        .createdAt(TimeUtil.relativeTimeFormatter(comment.getCreatedDate()))
        .isModified(comment.isModified())
        .likeCount(comment.getLikeCount())
        .isLiked(isLiked)
        .isAuthor(comment.isAuthor())
        .isMyComment(isMyComment)
        .replyListResponse(replyListResponse)
        .build();
  }
}
