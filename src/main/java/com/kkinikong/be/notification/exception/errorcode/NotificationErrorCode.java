package com.kkinikong.be.notification.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {
  INVALID_COMMUNITY_POST(HttpStatus.BAD_REQUEST, "COMMUNITY_LIKE 알림은 CommunityPost가 필요합니다."),
  INVALID_COMMENT(HttpStatus.BAD_REQUEST, "해당 알림에는 Comment가 필요합니다."),
  INVALID_PRODUCT_NAME(HttpStatus.BAD_REQUEST, "올바른 정보예요 알림은 제품명이 필요합니다.");

  private final HttpStatus httpStatus;
  private final String message;
}
