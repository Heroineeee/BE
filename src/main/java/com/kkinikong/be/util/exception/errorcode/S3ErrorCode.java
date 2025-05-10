package com.kkinikong.be.util.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum S3ErrorCode implements ErrorCode {
  S3_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S3 업로드에 실패했습니다."),
  FILE_FORMAT_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다."),
  FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "파일 크기가 초과했습니다."),
  S3_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S3 삭제에 실패했습니다."),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
