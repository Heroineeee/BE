package com.kkinikong.be.convenience.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class ConvenienceException extends RuntimeException {
  private final ErrorCode errorCode;
}
