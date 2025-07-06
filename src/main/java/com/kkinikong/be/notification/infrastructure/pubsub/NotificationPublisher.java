package com.kkinikong.be.notification.infrastructure.pubsub;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.notification.dto.NotificationMessage;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {
  private final RedisTemplate<String, Object> pubSubRedisTemplate;
  private final ChannelTopic channelTopic;
  private final ObjectMapper objectMapper;

  public void publish(NotificationMessage message) {
    try {
      String json = objectMapper.writeValueAsString(message);
      pubSubRedisTemplate.convertAndSend(channelTopic.getTopic(), json);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("알림 직렬화 실패", e);
    }
  }
}
