package com.kkinikong.be.user.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
  USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 유저입니다."),
  INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "잘못된 계정 정보입니다."),
  DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다."),
  USER_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "권한이 없습니다."),
  USER_ALREADY_DELETED(HttpStatus.NOT_FOUND, "이미 탈퇴한 유저입니다."),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
