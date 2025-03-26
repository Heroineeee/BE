package com.utopia.utopia_be.post.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.utopia.utopia_be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class PostException extends RuntimeException {
  private final ErrorCode errorCode;
}
