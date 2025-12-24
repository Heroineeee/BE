package com.kkinikong.be.cache.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.data.redis.domain.geo.Metrics;
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

  // 가맹점 위치 정보 단건 저장
  public void saveStoreLocation(Long storeId, Double latitude, Double longitude) {
    String key = RedisKey.STORE_LOCATIONS_KEY.getKey();
    redisTemplate.opsForGeo().add(key, new Point(longitude, latitude), storeId.toString());
  }

  // 주변 가맹점 ID 리스트 조회
  public List<Long> findNearbyStoreIds(Double latitude, Double longitude, Double radiusMeters) {
    String key = RedisKey.STORE_LOCATIONS_KEY.getKey();

    GeoResults<RedisGeoCommands.GeoLocation<Object>> results =
        redisTemplate
            .opsForGeo()
            .search(
                key,
                GeoReference.fromCoordinate(longitude, latitude),
                new Distance(radiusMeters, Metrics.METERS),
                RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().sortAscending());

    if (results == null) return List.of();

    return results.getContent().stream()
        .map(res -> Long.parseLong(res.getContent().getName().toString()))
        .toList();
  }

  // 가맹점 삭제 시 Geo 데이터도 삭제
  public void removeStoreLocation(Long storeId) {
    redisTemplate.opsForZSet().remove(RedisKey.STORE_LOCATIONS_KEY.getKey(), storeId.toString());
  }
}
