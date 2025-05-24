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
  CONVENIENCE_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "편의점 정보 게시글을 찾을 수 없습니다."),
  CONVENIENCE_POST_NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "편의점 정보 게시글의 작성자가 아닙니다."),
  NOT_ALLOWED_TO_SELECT_OWN_POST(HttpStatus.FORBIDDEN, "본인이 작성한 편의점 정보 게시글에서는 선택할 수 없습니다."),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
