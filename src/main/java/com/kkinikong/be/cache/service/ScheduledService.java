package com.kkinikong.be.cache.service;

import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.cache.type.RedisKey;
import com.kkinikong.be.community.repository.communityPost.CommunityPostRepository;
import com.kkinikong.be.store.repository.store.StoreRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledService {

  private final RedisTempleCacheService redisTempleCacheService;
  private final StoreRepository storeRepository;
  private final CommunityPostRepository communityPostRepository;

  @Scheduled(cron = "0 0 * * * *") // 매시간 0분에 실행
  @Transactional
  public void syncStoreViewCount() {
    Map<Object, Object> storesViewCounts =
        redisTempleCacheService.getViewCounts(RedisKey.STORE_VIEWS_KEY);
    if (storesViewCounts == null || storesViewCounts.isEmpty()) {
      return;
    }

    storesViewCounts.forEach(
        (k, v) -> {
          Long storeId = Long.parseLong(k.toString());
          Long viewCount = v == null ? 0L : Long.parseLong(v.toString());
          storeRepository.incrementViews(storeId, viewCount);
        });

    redisTempleCacheService.clearViewCounts(RedisKey.STORE_VIEWS_KEY);
  }

  @Scheduled(cron = "0 10 * * * *") // 매시간 10분에 실행
  @Transactional
  public void syncCommunityPostViewCount() {
    Map<Object, Object> communityPostsViewCounts =
        redisTempleCacheService.getViewCounts(RedisKey.COMMUNITY_POST_VIEWS_KEY);
    if (communityPostsViewCounts == null || communityPostsViewCounts.isEmpty()) {
      return;
    }

    communityPostsViewCounts.forEach(
        (k, v) -> {
          Long storeId = Long.parseLong(k.toString());
          Long viewCount = v == null ? 0L : Long.parseLong(v.toString());
          communityPostRepository.incrementViews(storeId, viewCount);
        });

    redisTempleCacheService.clearViewCounts(RedisKey.COMMUNITY_POST_VIEWS_KEY);
  }
}
