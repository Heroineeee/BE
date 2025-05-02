package com.kkinikong.be.store.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements ErrorCode {
  INVALID_CATEGORY_LABEL(HttpStatus.BAD_REQUEST, "유효하지 않은 카테고리 라벨입니다."),
  ;
  private final HttpStatus httpStatus;
  private final String message;
}
