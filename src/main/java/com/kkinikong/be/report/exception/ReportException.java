package com.kkinikong.be.report.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public class ReportException extends RuntimeException {
  private final ErrorCode errorCode;
}
