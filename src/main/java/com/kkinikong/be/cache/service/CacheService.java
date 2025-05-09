package com.kkinikong.be.cache.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CacheService {

  @CacheEvict(value = "store-id", allEntries = true)
  public String clearStoredStoreIdCache() {
    return "All Kakao Place IDs cleared from cache.";
  }

  @CacheEvict(value = "storeOpeningHours", allEntries = true)
  public String clearStoredOpeningHoursCache() {
    return "All Google Store Opening Hours cleared from cache.";
  }
}
