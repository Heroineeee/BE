package com.kkinikong.be.cache.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.cache.type.RedisKey;

@Service
@Slf4j
public class RedisTemplateCacheService {

  @Autowired private RedisTemplate<String, Object> redisTemplate;

  // 조회수 1 증가
  public void increaseViewCounts(Long id, RedisKey redisKey) {
    redisTemplate.opsForHash().increment(redisKey.getKey(), id.toString(), 1);
  }

  // 가맹점 전체 조회수 조회
  public Map<Object, Object> getViewCounts(RedisKey redisKey) {
    return redisTemplate.opsForHash().entries(redisKey.getKey());
  }

  // 가맹점 전체 조회수 삭제
  public void clearViewCounts(RedisKey redisKey) {
    redisTemplate.delete(redisKey.getKey());
  }

  // 최근 검색어 저장
  public void saveRecentSearch(long userId, String keyword) {
    String key = generateRecentSearchKey(userId);

    redisTemplate.opsForList().remove(key, 0, keyword); // 중복 제거
    redisTemplate.opsForList().leftPush(key, keyword); // 최근 검색어 추가
    redisTemplate.opsForList().trim(key, 0, 4); // 최대 5개 저장
  }

  // 최근 검색어 조회
  public List<String> getRecentSearches(long userId) {
    String key = generateRecentSearchKey(userId);

    if (!redisTemplate.hasKey(key)) {
      return List.of();
    }

    List<Object> recentSearches = redisTemplate.opsForList().range(key, 0, -1);
    return recentSearches.stream().map(Object::toString).toList();
  }

  // 최근 검색어 삭제
  public void deleteRecentSearches(long userId, String keyword) {
    String key = generateRecentSearchKey(userId);

    redisTemplate.opsForList().remove(key, 0, keyword);
  }

  private static String generateRecentSearchKey(long userId) {
    String key = RedisKey.RECENT_SEARCHES_KEY.getKey() + ":" + userId;
    return key;
  }
}
