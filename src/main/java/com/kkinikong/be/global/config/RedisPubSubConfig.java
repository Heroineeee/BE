package com.kkinikong.be.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.kkinikong.be.notification.infrastructure.pubsub.NotificationSubscriber;

@Configuration
public class RedisPubSubConfig {
  public static final String NOTIFICATION_CHANNEL = "notificationChannel";

  // Redis 채널 토픽 등록
  @Bean
  public ChannelTopic notificationChannel() {
    return new ChannelTopic(NOTIFICATION_CHANNEL);
  }

  // Redis Pub/Sub 수신 컨테이너
  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer(
      RedisConnectionFactory connectionFactory,
      NotificationSubscriber subscriber,
      ChannelTopic notificationChannel) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.addMessageListener(subscriber, notificationChannel);
    return container;
  }
}
