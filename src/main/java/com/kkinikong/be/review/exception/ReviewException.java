package com.kkinikong.be.review.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class ReviewException extends RuntimeException {
  private final ErrorCode errorCode;
}
