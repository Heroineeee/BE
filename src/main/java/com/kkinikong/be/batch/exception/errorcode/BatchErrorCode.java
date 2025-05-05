package com.kkinikong.be.batch.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum BatchErrorCode implements ErrorCode {
  INVALID_CATEGORY_LABEL(HttpStatus.BAD_REQUEST, "유효하지 않은 카테고리 라벨입니다."),
  INVALID_ADDRESS_FORMAT(HttpStatus.BAD_REQUEST, "주소 형식이 올바르지 않습니다."),
  EMPTY_FILE(HttpStatus.BAD_REQUEST, "업로드된 파일이 비어 있습니다."),
  FILE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CSV 파일 저장에 실패했습니다."),
  FILE_HASH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 해시 생성에 실패했습니다."),
  JOB_EXECUTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "배치 Job 실행에 실패했습니다."),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
