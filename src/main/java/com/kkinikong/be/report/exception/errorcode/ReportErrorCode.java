package com.kkinikong.be.report.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ReportErrorCode implements ErrorCode {
  REPORT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 신고한 가게입니다."),
  ;
  private final HttpStatus httpStatus;
  private final String message;
}
