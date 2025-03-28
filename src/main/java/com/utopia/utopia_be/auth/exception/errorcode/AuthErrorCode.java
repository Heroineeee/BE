package com.utopia.utopia_be.auth.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.utopia.utopia_be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
  LOGIN_TYPE_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "지원하지 않는 로그인 타입입니다."),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
