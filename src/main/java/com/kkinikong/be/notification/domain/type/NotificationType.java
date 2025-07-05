package com.kkinikong.be.notification.domain.type;

import lombok.Getter;

@Getter
public enum NotificationType {
  COMMUNITY_LIKE("게시글 좋아요"),
  COMMUNITY_COMMENT("게시글 댓글"),
  COMMENT_LIKE("댓글 좋아요"),
  COMMENT_COMMENT("댓글에 답글"),
  CONVENIENCE_CORRECT_INFO("올바른 정보예요");

  private final String label;

  NotificationType(String label) {
    this.label = label;
  }
}
