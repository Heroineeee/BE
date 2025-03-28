package com.utopia.utopia_be.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.utopia.utopia_be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class AuthException extends RuntimeException {
  private final ErrorCode errorCode;
}
