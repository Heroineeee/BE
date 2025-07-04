package com.kkinikong.be.community.dto.response;

public record CommentResponse(Long id) {
  public static CommentResponse from(Long id) {
    return new CommentResponse(id);
  }
}
