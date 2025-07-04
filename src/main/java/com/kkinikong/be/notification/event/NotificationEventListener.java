package com.kkinikong.be.notification.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.notification.infrastructure.sse.SseNotificationService;
import com.kkinikong.be.notification.util.NotificationFactory;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

  private final SseNotificationService notificationService;
  private final NotificationFactory notificationFactory;

  @EventListener
  public void handleNotificationEvent(NotificationEvent event) {
    String content =
        notificationFactory.createContent(
            event.getType(), event.getSenderNickname(), event.getTarget());

    notificationService.send(
        event.getReceiver(), event.getType(), content, event.getTargetId(), event.getRedirectUrl());
  }
}
