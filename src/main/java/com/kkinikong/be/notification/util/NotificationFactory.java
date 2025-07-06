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

  public String createContent(NotificationType type, Object target) {
    return switch (type) {
      case COMMUNITY_LIKE -> buildCommunityLike(target);
      case COMMENT_LIKE -> buildCommentLike(target);
      case COMMUNITY_COMMENT -> buildCommunityComment(target);
      case COMMENT_COMMENT -> buildCommentReply(target);
      case CONVENIENCE_CORRECT_INFO -> buildCorrectInfo(target);
    };
  }

  private String buildCommunityLike(Object post) {
    if (post instanceof CommunityPost communityPost) {
      return String.format("‘%s’ 게시글에 좋아요를 남겼어요.", shorten(communityPost.getContent()));
    }
    throw new NotificationException(NotificationErrorCode.INVALID_COMMUNITY_POST);
  }

  private String buildCommentLike(Object comment) {
    if (comment instanceof Comment c) {
      return String.format("‘%s’ 댓글에 좋아요를 남겼어요.", shorten(c.getContent()));
    }
    throw new NotificationException(NotificationErrorCode.INVALID_COMMENT);
  }

  private String buildCommunityComment(Object comment) {
    if (comment instanceof Comment c) {
      return String.format("게시글에 댓글을 남겼어요. ‘%s’", shorten(c.getContent()));
    }
    throw new NotificationException(NotificationErrorCode.INVALID_COMMENT);
  }

  private String buildCommentReply(Object comment) {
    if (comment instanceof Comment c) {
      return String.format("답글을 남겼어요. ‘%s’", shorten(c.getContent()));
    }
    throw new NotificationException(NotificationErrorCode.INVALID_COMMENT);
  }

  private String buildCorrectInfo(Object content) {
    if (content instanceof String productName) {
      return String.format("‘%s’ 글에 올바른 정보예요가 달렸어요.", shorten(productName));
    }
    throw new NotificationException(NotificationErrorCode.INVALID_PRODUCT_NAME);
  }

  private String shorten(String text) {
    return text.length() > 20 ? text.substring(0, 20) + "…" : text;
  }
}
