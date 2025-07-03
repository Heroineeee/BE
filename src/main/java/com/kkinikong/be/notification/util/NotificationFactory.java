package com.kkinikong.be.notification.util;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.community.domain.Comment;
import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.notification.domain.type.NotificationType;
import com.kkinikong.be.notification.exception.NotificationException;
import com.kkinikong.be.notification.exception.errorcode.NotificationErrorCode;

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
    throw new NotificationException(NotificationErrorCode.INVALID_COMMUNITY_POST);
  }

  private String buildCommentLike(String nickname, Object comment) {
    if (comment instanceof Comment c) {
      return String.format("%s님이 댓글에 좋아요를 남겼어요. ‘%s’", nickname, shorten(c.getContent()));
    }
    throw new NotificationException(NotificationErrorCode.INVALID_COMMENT);
  }

  private String buildCommunityComment(String nickname, Object comment) {
    if (comment instanceof Comment c) {
      return String.format("%s님이 게시글에 댓글을 남겼어요. ‘%s’", nickname, shorten(c.getContent()));
    }
    throw new NotificationException(NotificationErrorCode.INVALID_COMMENT);
  }

  private String buildCommentReply(String nickname, Object comment) {
    if (comment instanceof Comment c) {
      return String.format("%s님이 답글을 남겼어요. ‘%s’", nickname, shorten(c.getContent()));
    }
    throw new NotificationException(NotificationErrorCode.INVALID_COMMENT);
  }

  private String buildCorrectInfo(String nickname, Object content) {
    if (content instanceof String productName) {
      return String.format("%s님이 ‘%s’ 글에 올바른 정보예요가 달렸어요.", nickname, shorten(productName));
    }
    throw new NotificationException(NotificationErrorCode.INVALID_PRODUCT_NAME);
  }

  private String shorten(String text) {
    return text.length() > 20 ? text.substring(0, 20) + "…" : text;
  }
}
