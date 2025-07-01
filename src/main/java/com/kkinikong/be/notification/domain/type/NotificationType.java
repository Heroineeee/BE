package com.kkinikong.be.notification.domain.type;

import lombok.Getter;

@Getter
public enum NotificationType {
  COMMUNITY_LIKE("게시글 좋아요", "게시글에 좋아요를 남겼어요"),
  COMMUNITY_COMMENT("게시글 댓글", "게시글에 댓글을 남겼어요"),
  COMMENT_LIKE("댓글 좋아요", "댓글에 좋아요를 남겼어요"),
  COMMENT_COMMENT("댓글에 답글", "댓글에 답글을 남겼어요"),
  CONVENIENCE_CORRECT_INFO("올바른 정보예요", "올바른 정보예요를 눌렀어요");

  private final String label;
  private final String defaultMessage;

  NotificationType(String label, String defaultMessage) {
    this.label = label;
    this.defaultMessage = defaultMessage;
  }
}
