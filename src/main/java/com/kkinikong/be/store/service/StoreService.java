package com.kkinikong.be.store.service;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.dto.response.StoreExternalLinkResponse;
import com.kkinikong.be.store.dto.response.StoreInfoResponse;
import com.kkinikong.be.store.exception.StoreException;
import com.kkinikong.be.store.exception.errorcode.StoreErrorCode;
import com.kkinikong.be.store.repository.StoreRepository;
import com.kkinikong.be.store.util.kakao.StoreKakaoApiClient;
import com.kkinikong.be.user.exception.UserException;
import com.kkinikong.be.user.exception.errorcode.UserErrorCode;
import com.kkinikong.be.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

  private final StoreRepository storeRepository;
  private final UserRepository userRepository;
  private final StoreCacheService storeCacheService;
  private final StoreKakaoApiClient storeKakaoApiClient;

  private static final String NO_INFO = "NO_INFO";

  public StoreInfoResponse getStoreInfo(Long storeId) {
    Store store = findStoreOrThrow(storeId);

    return StoreInfoResponse.builder()
        .storeId(storeId)
        .storeCategory(store.getCategory().getLabel())
        .storeName(store.getName())
        .storeAddress(store.getAddress())
        .storeRating(store.getRatingAvg())
        .storeReviewCount(store.getReviewCount())
        .storeScrapCount(store.getScrapCount())
        .storeUpdatedDate(store.getUpdatedDate())
        .build();
  }

  public StoreExternalLinkResponse getStoreExternalLink(Long storeId) {
    Store store = findStoreOrThrow(storeId);

    // 캐시에서 카카오 ID 조회 후 없으면 카카오 API 호출 후 캐싱
    String kakaoPlaceId =
        storeCacheService
            .getStoreKakaoId(storeId)
            .orElseGet(() -> fetchAndCacheKakaoPlaceId(storeId, store));

    // 캐시에 저장된 카카오 ID가 NO_INFO인 경우 링크를 제공하지 않음
    boolean hasInfo = !NO_INFO.equals(kakaoPlaceId);

    return StoreExternalLinkResponse.builder()
        .menuUrl(hasInfo ? buildMenuUrl(kakaoPlaceId) : NO_INFO)
        .directionUrl(hasInfo ? buildDirectionUrl(kakaoPlaceId) : NO_INFO)
        .build();
  }

  private void findUserOrThrow(Long userId) {
    userRepository
        .findUserById(userId)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
  }

  private String fetchAndCacheKakaoPlaceId(Long storeId, Store store) {
    String kakaoPlaceId = storeKakaoApiClient.getKakaoLocalSearch(store);

    if (kakaoPlaceId == null) {
      kakaoPlaceId = NO_INFO;
      // 카카오 API에서 결과가 없을 경우는 더 짧게 캐싱
      storeCacheService.saveStoreKakaoId(storeId, kakaoPlaceId, Duration.ofDays(15));
    } else {
      storeCacheService.saveStoreKakaoId(storeId, kakaoPlaceId, Duration.ofDays(30));
    }
    return kakaoPlaceId;
  }

  private String buildDirectionUrl(String placeId) {
    return "https://map.kakao.com/link/to/" + placeId;
  }

  private String buildMenuUrl(String placeId) {
    return "https://place.map.kakao.com/" + placeId + "#menuInfo";
  }

  private Store findStoreOrThrow(Long storeId) {
    return storeRepository
        .findStoreById(storeId)
        .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));
  }
}
