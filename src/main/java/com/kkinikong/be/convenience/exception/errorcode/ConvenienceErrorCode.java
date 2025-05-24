package com.kkinikong.be.convenience.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ConvenienceErrorCode implements ErrorCode {
  CONVENIENCE_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "편의점 정보 게시글을 찾을 수 없습니다."),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
