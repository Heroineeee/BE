package com.kkinikong.be.convenience.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ConvenienceErrorCode implements ErrorCode {
  OPEN_AI_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OpenAI API 요청에 실패했습니다."),
  PARSE_CHOICES_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OpenAI API 응답 파싱에 실패했습니다."),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
