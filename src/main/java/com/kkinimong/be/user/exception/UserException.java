package com.kkinimong.be.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinimong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class UserException extends RuntimeException {
  private final ErrorCode errorCode;
}
