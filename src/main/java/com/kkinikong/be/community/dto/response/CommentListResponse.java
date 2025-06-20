package com.kkinikong.be.community.dto.response;

import java.util.List;

import com.kkinikong.be.community.domain.Comment;
import com.kkinikong.be.community.util.TimeUtil;

public record CommentListResponse(
    Long commentId,
    String content,
    String nickname,
    String createdAt,
    boolean isModified,
    Long likeCount,
    boolean isLiked,
    boolean isAuthor,
    List<CommentListResponse> replyListResponse) {

  public static CommentListResponse from(
      Comment comment,
      boolean isLiked,
      boolean isAuthor,
      List<CommentListResponse> replyListResponse) {
    return new CommentListResponse(
        comment.getId(),
        comment.getContent(),
        comment.getUser().getNickname(),
        TimeUtil.relativeTimeFomatter(comment.getCreatedDate()),
        !(comment.getCreatedDate() == comment.getModifiedDate()),
        comment.getLikeCount(),
        isLiked,
        isAuthor,
        replyListResponse);
  }
}
