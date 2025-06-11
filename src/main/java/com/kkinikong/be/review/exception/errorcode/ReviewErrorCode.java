package com.kkinikong.be.review.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {
  REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
  REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 리뷰를 작성했습니다."),
  REVIEW_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "리뷰의 작성자가 아닙니다."),
  REVIEW_IMAGE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "리뷰 이미지가 이미 존재합니다."),
  ;
  private final HttpStatus httpStatus;
  private final String message;
}
