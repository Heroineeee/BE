package com.kkinikong.be.notification.infrastructure.pubsub;

import java.nio.charset.StandardCharsets;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.notification.dto.NotificationMessage;
import com.kkinikong.be.notification.infrastructure.sse.SseNotificationService;
import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.exception.UserException;
import com.kkinikong.be.user.exception.errorcode.UserErrorCode;
import com.kkinikong.be.user.repository.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSubscriber implements MessageListener {

  private final ObjectMapper objectMapper;
  private final SseNotificationService sseNotificationService;
  private final UserRepository userRepository;

  @Override
  public void onMessage(Message message, byte[] pattern) {
    try {
      String json = new String(message.getBody(), StandardCharsets.UTF_8);
      NotificationMessage notificationMessage =
          objectMapper.readValue(json, NotificationMessage.class);
      User receiver = getUserOrThrow(notificationMessage.receiverId());
      sseNotificationService.send(
          receiver,
          notificationMessage.type(),
          notificationMessage.senderNickname(),
          notificationMessage.content(),
          notificationMessage.targetId(),
          notificationMessage.redirectUrl());

    } catch (Exception e) {
      log.error("❌ Redis 메시지 처리 실패", e);
    }
  }

  private User getUserOrThrow(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
  }
}
