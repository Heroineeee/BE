package com.kkinikong.be.convenience.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ConvenienceErrorCode implements ErrorCode {
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
