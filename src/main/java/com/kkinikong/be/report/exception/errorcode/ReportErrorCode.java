package com.kkinikong.be.report.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ReportErrorCode implements ErrorCode {
  STORE_REPORT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 신고한 가게는 7일 이내에 다시 신고할 수 없습니다."),
  REVIEW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 신고한 리뷰입니다."),
  REVIEW_REPORT_SELF(HttpStatus.BAD_REQUEST, "자기 자신을 신고할 수 없습니다.");

  private final HttpStatus httpStatus;
  private final String message;
}
