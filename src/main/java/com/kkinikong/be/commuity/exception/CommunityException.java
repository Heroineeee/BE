package com.kkinikong.be.commuity.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.commuity.exception.errorcode.CommunityErrorCode;

@Getter
@RequiredArgsConstructor
public class CommunityException extends RuntimeException {
  private final CommunityErrorCode errorCode;
}
