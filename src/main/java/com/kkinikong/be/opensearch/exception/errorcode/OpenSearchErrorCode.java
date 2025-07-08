package com.kkinikong.be.opensearch.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum OpenSearchErrorCode implements ErrorCode {
  CREATE_INDEX_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "인덱스 생성에 실패했습니다."),
  FAILED_TO_SAVE_INDEX(HttpStatus.INTERNAL_SERVER_ERROR, "인덱스 저장에 실패했습니다."),
  FAILED_TO_DELETE_INDEX(HttpStatus.INTERNAL_SERVER_ERROR, "인덱스 삭제에 실패했습니다."),
  FAILED_TO_SEARCH_INDEX(HttpStatus.INTERNAL_SERVER_ERROR, "인덱스 검색에 실패했습니다."),
  FAILED_TO_RESET_INDEX(HttpStatus.INTERNAL_SERVER_ERROR, "인덱스 초기화에 실패했습니다."),
  FAILED_TO_BULK_INDEX(HttpStatus.INTERNAL_SERVER_ERROR, "인덱스 대량 저장에 실패했습니다."),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
