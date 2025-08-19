package com.kkinikong.be.notification.dto.response;

import com.kkinikong.be.global.util.TimeUtil;
import com.kkinikong.be.notification.domain.Notification;
import com.kkinikong.be.notification.domain.type.NotificationType;

public record NotificationResponse(
    Long notificationId,
    NotificationType type,
    String senderNickname,
    String content,
    String redirectUrl,
    Long targetId,
    String createdAt,
    boolean isRead) {
  public static NotificationResponse from(Notification notification) {
    return new NotificationResponse(
        notification.getId(),
        notification.getNotificationType(),
        notification.getSenderNickname(),
        notification.getContent(),
        notification.getRedirectUrl(),
        notification.getTargetId(),
        TimeUtil.relativeTimeFormatter(notification.getCreatedDate()),
        notification.isRead());
  }
}
