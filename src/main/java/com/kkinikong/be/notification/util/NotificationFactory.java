package com.kkinikong.be.notification.util;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.community.domain.Comment;
import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.notification.domain.type.NotificationType;

@Component
@RequiredArgsConstructor
public class NotificationFactory {

  public String createContent(NotificationType type, String senderNickname, Object target) {
    return switch (type) {
      case COMMUNITY_LIKE -> buildCommunityLike(senderNickname, target);
      case COMMENT_LIKE -> buildCommentLike(senderNickname, target);
      case COMMUNITY_COMMENT -> buildCommunityComment(senderNickname, target);
      case COMMENT_COMMENT -> buildCommentReply(senderNickname, target);
      case CONVENIENCE_CORRECT_INFO -> buildCorrectInfo(senderNickname, target);
    };
  }

  private String buildCommunityLike(String nickname, Object post) {
    if (post instanceof CommunityPost communityPost) {
      return String.format(
          "%s님이 ‘%s’ 게시글에 좋아요를 남겼어요.", nickname, shorten(communityPost.getContent()));
    }
    throw new IllegalArgumentException("COMMUNITY_LIKE 알림은 CommunityPost가 필요합니다.");
  }

  private String buildCommentLike(String nickname, Object comment) {
    if (comment instanceof Comment c) {
      return String.format("%s님이 댓글에 좋아요를 남겼어요. ‘%s’", nickname, shorten(c.getContent()));
    }
    throw new IllegalArgumentException("COMMENT_LIKE 알림은 Comment가 필요합니다.");
  }

  private String buildCommunityComment(String nickname, Object comment) {
    if (comment instanceof Comment c) {
      return String.format("%s님이 게시글에 댓글을 남겼어요. ‘%s’", nickname, shorten(c.getContent()));
    }
    throw new IllegalArgumentException("COMMUNITY_COMMENT 알림은 Comment가 필요합니다.");
  }

  private String buildCommentReply(String nickname, Object comment) {
    if (comment instanceof Comment c) {
      return String.format("%s님이 답글을 남겼어요. ‘%s’", nickname, shorten(c.getContent()));
    }
    throw new IllegalArgumentException("COMMENT_COMMENT 알림은 Comment가 필요합니다.");
  }

  private String buildCorrectInfo(String nickname, Object content) {
    if (content instanceof String productName) {
      return String.format("%s님이 ‘%s’ 글에 올바른 정보예요가 달렸어요.", nickname, shorten(productName));
    }
    throw new IllegalArgumentException("CONVENIENCE_CORRECT_INFO 알림은 String 제품명이 필요합니다.");
  }

  private String shorten(String text) {
    return text.length() > 20 ? text.substring(0, 20) + "…" : text;
  }
}
