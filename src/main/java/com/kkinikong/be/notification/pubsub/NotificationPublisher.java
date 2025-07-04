package com.kkinikong.be.notification.pubsub;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.notification.dto.response.NotificationMessage;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {
  private final RedisTemplate<String, Object> pubSubRedisTemplate;
  private final ChannelTopic channelTopic;

  public void publish(NotificationMessage message) {
    pubSubRedisTemplate.convertAndSend(channelTopic.getTopic(), message);
  }
}
