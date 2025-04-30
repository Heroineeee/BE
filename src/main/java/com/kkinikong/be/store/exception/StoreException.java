package com.kkinikong.be.store.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class StoreException extends RuntimeException {
  private final ErrorCode errorCode;
}
