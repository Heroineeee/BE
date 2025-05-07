package com.kkinikong.be.store.service;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreCacheService {

  private final RedisTemplate<String, Object> redisTemplate;

  private static final String KEY_PREFIX = "store-id:";

  public static final Duration EXTERNAL_LINK_CACHE_TTL = Duration.ofDays(30); // 30일 동안 캐싱

  public void saveStoreNaverId(Long storeId, String naverId) {
    String key = KEY_PREFIX + storeId;
    redisTemplate.opsForValue().set(key, naverId, EXTERNAL_LINK_CACHE_TTL);
  }

  public Optional<String> getStoreNaverId(Long storeId) {
    String key = KEY_PREFIX + storeId;
    Object value = redisTemplate.opsForValue().get(key);
    return Optional.ofNullable((String) value);
  }
}
