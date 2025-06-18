package com.kkinikong.be.feedback.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class FeedbackException extends RuntimeException {
  private final ErrorCode errorCode;
}
