package com.kkinikong.be.store.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements ErrorCode {
  STORE_NOT_FOUND(HttpStatus.BAD_REQUEST, "가맹점을 찾을 수 없습니다."),
  KAKAO_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 API 호출에 실패했습니다."),
  KAKAO_API_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 API 호출 결과를 파싱하는데 실패했습니다."),
  GOOGLE_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "구글 API 호출에 실패했습니다."),
  JSON_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 파싱에 실패했습니다."),
  CONVERT_TIME_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "시간 변환에 실패했습니다."),
  MISSING_GPS_FOR_SEARCH(HttpStatus.BAD_REQUEST, "검색을 위해 위경도 정보가 필요합니다."),

  ///  가맹점 스크랩 관련 에러
  SCRAP_NOT_FOUND(HttpStatus.NOT_FOUND, "스크랩 정보가 없습니다."),
  ALREADY_SCRAPPED(HttpStatus.BAD_REQUEST, "이미 스크랩한 가맹점입니다."),

  /// StoreUploadService 관련 에러
  CSV_PARSE_FAILED(HttpStatus.BAD_REQUEST, "CSV 파일 파싱에 실패했습니다."),
  INVALID_ADDRESS_FORMAT(HttpStatus.BAD_REQUEST, "주소 형식이 올바르지 않습니다."),
  INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "유효하지 않은 카테고리입니다."),
  STORE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "가맹점 저장에 실패했습니다."),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
