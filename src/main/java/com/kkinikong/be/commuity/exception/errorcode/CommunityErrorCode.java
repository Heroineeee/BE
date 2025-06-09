package com.kkinikong.be.commuity.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CommunityErrorCode implements ErrorCode {
  COMMUNITY_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "커뮤니티 게시글을 찾을 수 없습니다.");

  private final HttpStatus httpStatus;
  private final String message;
}
