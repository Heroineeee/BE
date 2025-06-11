package com.kkinikong.be.image.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class S3Exception extends RuntimeException {
  private final ErrorCode errorCode;
}
