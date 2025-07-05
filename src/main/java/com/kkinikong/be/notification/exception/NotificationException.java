package com.kkinikong.be.notification.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.notification.exception.errorcode.NotificationErrorCode;

@Getter
@RequiredArgsConstructor
public class NotificationException extends RuntimeException {
  private final NotificationErrorCode errorCode;
}
