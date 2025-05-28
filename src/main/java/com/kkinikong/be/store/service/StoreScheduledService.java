package com.kkinikong.be.store.service;

import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.store.repository.store.StoreRepository;

// 💬 코드 리뷰용 주석 시작
@Slf4j
@Service
@RequiredArgsConstructor
public class StoreScheduledService {

  private final StoreCacheService storeCacheService;
  private final StoreRepository storeRepository;

  @Scheduled(cron = "0 0 * * * *") // 매시간 0분에 실행
  @Transactional
  public void syncStoreViewCount() {
    Map<Object, Object> storesViewCounts = storeCacheService.getStoresViewCounts();
    if (storesViewCounts == null || storesViewCounts.isEmpty()) {
      return;
    }

    storesViewCounts.forEach(
        (k, v) -> {
          Long storeId = Long.parseLong(k.toString());
          Long viewCount = v == null ? 0L : Long.parseLong(v.toString());
          storeRepository.incrementViews(storeId, viewCount);
        });

    storeCacheService.clearStoresViewCounts();
  }
}
// 💬 코드 리뷰용 주석 종료
