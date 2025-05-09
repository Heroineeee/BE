package com.kkinikong.be.store.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.dto.response.StoreExternalLinkResponse;
import com.kkinikong.be.store.dto.response.StoreInfoResponse;
import com.kkinikong.be.store.exception.StoreException;
import com.kkinikong.be.store.exception.errorcode.StoreErrorCode;
import com.kkinikong.be.store.repository.StoreRepository;
import com.kkinikong.be.store.util.google.StoreGoogleApiClient;
import com.kkinikong.be.store.util.kakao.StoreKakaoApiClient;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StoreService {

  private final StoreRepository storeRepository;
  private final StoreKakaoApiClient storeKakaoApiClient;
  private final StoreGoogleApiClient storeGoogleApiClient;
  private final StoreCacheService storeCacheService;

  private static final String NO_INFO = "NO_INFO";

  public StoreInfoResponse getStoreInfo(Long storeId) {
    Store store = findStoreOrThrow(storeId);

    storeCacheService.increaseViewCounts(storeId);

    return StoreInfoResponse.builder()
        .storeId(storeId)
        .storeCategory(store.getCategory().getLabel())
        .storeName(store.getName())
        .storeAddress(store.getAddress())
        .storeWeeklyOpeningHours(storeGoogleApiClient.getStoreOpeningHours(store))
        .storeRating(store.getRatingAvg())
        .storeReviewCount(store.getReviewCount())
        .storeScrapCount(store.getScrapCount())
        .storeUpdatedDate(store.getUpdatedDate())
        .build();
  }

  @Cacheable(value = "store-id", key = "#storeId", unless = "#result == null")
  public StoreExternalLinkResponse getStoreExternalLink(Long storeId) {
    Store store = findStoreOrThrow(storeId);

    String kakaoPlaceId = fetchAndCacheKakaoPlaceId(store);
    boolean hasInfo = !NO_INFO.equals(kakaoPlaceId);

    return StoreExternalLinkResponse.builder()
        .menuUrl(hasInfo ? buildMenuUrl(kakaoPlaceId) : buildNaverUrl(store.getName()))
        .directionUrl(hasInfo ? buildDirectionUrl(kakaoPlaceId) : buildNaverUrl(store.getName()))
        .build();
  }

  private String fetchAndCacheKakaoPlaceId(Store store) {
    String kakaoPlaceId = storeKakaoApiClient.getKakaoLocalSearch(store);
    if (kakaoPlaceId == null) {
      kakaoPlaceId = NO_INFO;
    }
    return kakaoPlaceId;
  }

  private String buildDirectionUrl(String placeId) {
    return "https://map.kakao.com/link/to/" + placeId;
  }

  private String buildMenuUrl(String placeId) {
    return "https://place.map.kakao.com/" + placeId + "#menuInfo";
  }

  private String buildNaverUrl(String placeName) {
    return "https://map.naver.com/v5/search/" + placeName;
  }

  private Store findStoreOrThrow(Long storeId) {
    return storeRepository
        .findStoreById(storeId)
        .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));
  }
}
