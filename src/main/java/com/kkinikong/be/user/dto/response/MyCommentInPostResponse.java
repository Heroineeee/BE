package com.kkinikong.be.user.dto.response;

import com.kkinikong.be.community.domain.Comment;

public record MyCommentInPostResponse(Long commentId, String content) {
  public static MyCommentInPostResponse from(Comment comment) {
    return new MyCommentInPostResponse(comment.getId(), comment.getContent());
  }
}
