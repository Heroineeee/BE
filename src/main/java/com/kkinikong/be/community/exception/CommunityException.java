package com.kkinikong.be.community.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.community.exception.errorcode.CommunityErrorCode;

@Getter
@RequiredArgsConstructor
public class CommunityException extends RuntimeException {
  private final CommunityErrorCode errorCode;
}
