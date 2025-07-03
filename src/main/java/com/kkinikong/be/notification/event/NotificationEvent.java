package com.kkinikong.be.notification.event;

import lombok.Builder;
import lombok.Getter;

import com.kkinikong.be.notification.domain.type.NotificationType;
import com.kkinikong.be.user.domain.User;

@Getter
public class NotificationEvent {

  private final User receiver;
  private final NotificationType type;
  private final String senderNickname;
  private final Object target;
  private final Long targetId;
  private final String redirectUrl;

  @Builder
  public NotificationEvent(
      User receiver,
      NotificationType type,
      String senderNickname,
      Object target,
      Long targetId,
      String redirectUrl) {
    this.receiver = receiver;
    this.type = type;
    this.senderNickname = senderNickname;
    this.target = target;
    this.targetId = targetId;
    this.redirectUrl = redirectUrl;
  }
}
