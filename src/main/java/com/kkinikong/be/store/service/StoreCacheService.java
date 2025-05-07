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

  public void saveStoreNaverId(Long storeId, String naverId, Duration ttl) {
    String key = KEY_PREFIX + storeId;
    redisTemplate.opsForValue().set(key, naverId, ttl);
  }

  public Optional<String> getStoreNaverId(Long storeId) {
    String key = KEY_PREFIX + storeId;
    Object value = redisTemplate.opsForValue().get(key);
    return Optional.ofNullable((String) value);
  }
}
