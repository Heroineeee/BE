package com.kkinimong.be.post.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinimong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCode {
  POST_NOT_FOUND(HttpStatus.NOT_FOUND, "Post not found"),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
