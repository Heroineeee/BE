package com.kkinikong.be.batch.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class BatchException extends RuntimeException {
  private final ErrorCode errorCode;
}
