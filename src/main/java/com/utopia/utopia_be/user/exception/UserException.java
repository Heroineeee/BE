package com.utopia.utopia_be.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.utopia.utopia_be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class UserException extends RuntimeException {
  private final ErrorCode errorCode;
}
