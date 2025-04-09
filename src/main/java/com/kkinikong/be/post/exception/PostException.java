package com.kkinikong.be.post.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class PostException extends RuntimeException {
  private final ErrorCode errorCode;
}
