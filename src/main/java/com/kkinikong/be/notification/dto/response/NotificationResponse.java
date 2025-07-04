package com.kkinikong.be.notification.dto.response;

import com.kkinikong.be.notification.domain.Notification;
import com.kkinikong.be.notification.domain.type.NotificationType;

public record NotificationResponse(
    Long notificationId, NotificationType type, String content, String redirectUrl, Long targetId) {
  public static NotificationResponse from(Notification notification) {
    return new NotificationResponse(
        notification.getId(),
        notification.getNotificationType(),
        notification.getContent(),
        notification.getRedirectUrl(),
        notification.getTargetId());
  }
}
