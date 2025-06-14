package com.kkinikong.be.community.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.exception.errorcode.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CommunityErrorCode implements ErrorCode {
  COMMUNITY_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "커뮤니티 게시글을 찾을 수 없습니다."),
  COMMUNITY_NOT_OWNER(HttpStatus.FORBIDDEN, "커뮤니티 게시글의 작성자가 아닙니다."),
  COMMUNITY_POST_IMAGE_SIZE_LIMIT(HttpStatus.BAD_REQUEST, "커뮤니티 게시글 이미지의 개수는 3개까지 가능합니다."),
  COMMUNITY_POST_IMAGE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "커뮤니티 게시글 이미지가 이미 존재합니다."),

  COMMENT_SIZE_LIMIT(HttpStatus.BAD_REQUEST, "댓글은 4000자 이하로 작성해주세요."),
  REPLY_SIZE_LIMIT(HttpStatus.BAD_REQUEST, "답글은 2000자 이하로 작성해주세요."),
  COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
  NOT_TOP_COMMENT(HttpStatus.BAD_REQUEST, "최상위 댓글이 아닙니다."),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
