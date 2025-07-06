package com.kkinikong.be.notification.infrastructure.pubsub;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.notification.dto.NotificationMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPublisher {
  private final RedisTemplate<String, Object> pubSubRedisTemplate;
  private final ChannelTopic channelTopic;
  private final ObjectMapper objectMapper;

  public void publish(NotificationMessage message) {
    pubSubRedisTemplate.convertAndSend(channelTopic.getTopic(), message);
  }
}
