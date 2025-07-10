package com.kkinikong.be.notification.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.notification.domain.type.NotificationType;
import com.kkinikong.be.notification.dto.NotificationMessage;
import com.kkinikong.be.notification.infrastructure.pubsub.NotificationPublisher;
import com.kkinikong.be.notification.util.NotificationFactory;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

  private final NotificationPublisher notificationPublisher;
  private final NotificationFactory notificationFactory;

  // 1. 게시글 좋아요 알림
  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handlePostLikeEvent(CommunityLikeEvent event) {
    publishNotification(
        NotificationType.COMMUNITY_LIKE,
        event.receiver().getId(),
        event.sender().getNickname(),
        event.post().getId(),
        "/community/post/" + event.post().getId(),
        event.post());
  }

  // 2. 게시글 댓글 알림
  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleCommentCreated(CommentEvent event) {
    publishNotification(
        NotificationType.COMMUNITY_COMMENT,
        event.receiver().getId(),
        event.sender().getNickname(),
        event.post().getId(),
        "/community/post/" + event.post().getId(),
        event.comment());
  }

  // 3. 댓글의 답글 알림
  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleReplyCreated(CommentReplyEvent event) {
    publishNotification(
        NotificationType.COMMENT_COMMENT,
        event.receiver().getId(),
        event.sender().getNickname(),
        event.post().getId(),
        "/community/post/" + event.post().getId(),
        event.reply());
  }

  // 4. 댓글 좋아요 알림
  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleCommentLiked(CommentLikeEvent event) {
    publishNotification(
        NotificationType.COMMENT_LIKE,
        event.receiver().getId(),
        event.sender().getNickname(),
        event.comment().getCommunityPost().getId(),
        "/community/post/" + event.comment().getCommunityPost().getId(),
        event.comment());
  }

  // 5. ‘올바른 정보예요’ 알림
  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleConvenienceInfo(ConvenienceInfoEvent event) {
    publishNotification(
        NotificationType.CONVENIENCE_CORRECT_INFO,
        event.post().getUser().getId(),
        event.sender().getNickname(),
        event.post().getId(),
        "/convenience/post/" + event.post().getId(),
        event.post().getName());
  }

  private void publishNotification(
      NotificationType type,
      Long receiverId,
      String senderNickname,
      Long targetId,
      String redirectUrl,
      Object target) {
    String content = notificationFactory.createContent(type, target);
    NotificationMessage message =
        new NotificationMessage(receiverId, type, senderNickname, content, redirectUrl, targetId);
    notificationPublisher.publish(message);
  }
}
