package com.kkinikong.be.store.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

// 💬 코드 리뷰용 주석 시작
@Service
@Slf4j
public class StoreCacheService {

  @Autowired private RedisTemplate<String, Object> redisTemplate;

  private static final String STORE_VIEWS_KEY = "store-views";

  // 조회수 1 증가
  public void increaseViewCounts(Long storeId) {
    redisTemplate.opsForHash().increment(STORE_VIEWS_KEY, storeId.toString(), 1);
  }

  // 단일 조회수 조회
  public Long getViewCounts(Long storeId) {
    Object value = redisTemplate.opsForHash().get(STORE_VIEWS_KEY, storeId.toString());
    return value == null ? 0L : Long.parseLong(value.toString());
  }

  // 전체 조회수 조회
  public Map<Object, Object> getStoresViewCounts() {
    return redisTemplate.opsForHash().entries("store-views");
  }

  // 전체 조회수 삭제
  public void clearStoresViewCounts() {
    redisTemplate.delete(STORE_VIEWS_KEY);
  }
}
// 💬 코드 리뷰용 주석 종료
