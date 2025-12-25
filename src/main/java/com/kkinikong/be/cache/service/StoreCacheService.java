package com.kkinikong.be.cache.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.repository.store.StoreRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreCacheService {

  private final StoreRepository storeRepository;
  private final RedisTemplateCacheService redisTemplateCacheService;

  @Transactional(readOnly = true)
  public String syncStoreLocations() {
    log.info("가맹점 위치 정보 전수 동기화 시작...");

    int pageSize = 1000;
    int pageNumber = 0;
    long totalCount = 0;

    while (true) {
      Page<Store> storePage = storeRepository.findAll(PageRequest.of(pageNumber, pageSize));

      if (storePage.isEmpty()) break;

      redisTemplateCacheService.saveStoreLocationsBulk(storePage.getContent());
      totalCount += storePage.getNumberOfElements();
      log.info("{}건 적재 완료...", totalCount);

      if (!storePage.hasNext()) break;
      pageNumber++;
    }
    return "성공적으로 " + totalCount + " 건의 위치 데이터를 동기화했습니다.";
  }
}
