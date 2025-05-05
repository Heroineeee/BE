package com.kkinikong.be.store.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements ErrorCode {
  ;
  private final HttpStatus httpStatus;
  private final String message;
}
