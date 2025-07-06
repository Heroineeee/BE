package com.kkinikong.be.notification.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.notification.dto.NotificationMessage;
import com.kkinikong.be.notification.infrastructure.pubsub.NotificationPublisher;
import com.kkinikong.be.notification.util.NotificationFactory;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

  private final NotificationPublisher notificationPublisher;
  private final NotificationFactory notificationFactory;

  @EventListener
  public void handleNotificationEvent(NotificationEvent event) {
    NotificationMessage message =
        new NotificationMessage(
            event.getReceiver().getId(),
            event.getType(),
            notificationFactory.createContent(event.getType(), event.getTarget()),
            event.getRedirectUrl(),
            event.getTargetId());
    notificationPublisher.publish(message);
  }
}
