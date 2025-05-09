package com.kkinikong.be.store.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements ErrorCode {
  MISSING_GPS_FOR_DISTANCE(HttpStatus.BAD_REQUEST, "거리순 정렬에는 위경도 정보가 필요합니다."),
  STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 가맹점입니다."),
  ;
  private final HttpStatus httpStatus;
  private final String message;
}
