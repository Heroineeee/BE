package com.kkinikong.be.community.dto.response;

import java.util.List;

import com.kkinikong.be.community.domain.Comment;
import com.kkinikong.be.community.util.TimeUtil;
import com.kkinikong.be.user.utils.UserNicknameUtil;

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
    return new CommentListResponse(
        comment.getId(),
        comment.getContent(),
        UserNicknameUtil.displayNickname(comment.getUser()),
        TimeUtil.relativeTimeFormatter(comment.getCreatedDate()),
        comment.isDeleted(),
        comment.isModified(),
        comment.getLikeCount(),
        isLiked,
        comment.isAuthor(),
        isMyComment,
        replyListResponse);
  }
}
