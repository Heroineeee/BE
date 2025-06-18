package com.kkinikong.be.global.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableCaching
@Configuration
public class RedisCacheConfig {

  @Bean
  public CacheManager contentCacheManager(RedisConnectionFactory cf) {
    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

    RedisCacheConfiguration redisCacheConfiguration =
        RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer()))
            .entryTtl(Duration.ofDays(30)); // 캐시 수명 30일

    // 인기 검색어: TTL 30분
    cacheConfigurations.put(
        "community-popular-posts", redisCacheConfiguration.entryTtl(Duration.ofMinutes(30)));

    return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(cf)
        .cacheDefaults(redisCacheConfiguration)
        .withInitialCacheConfigurations(cacheConfigurations)
        .build();
  }
}
