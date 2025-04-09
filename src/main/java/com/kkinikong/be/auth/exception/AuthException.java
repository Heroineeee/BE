package com.kkinikong.be.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class AuthException extends RuntimeException {
  private final ErrorCode errorCode;
}
