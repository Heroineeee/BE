package com.kkinimong.be.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinimong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class AuthException extends RuntimeException {
  private final ErrorCode errorCode;
}
