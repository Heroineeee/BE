package com.kkinikong.be.store.service;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.review.repository.reviewtag.ReviewTagRepository;
import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.StoreScrap;
import com.kkinikong.be.store.domain.type.Category;
import com.kkinikong.be.store.domain.type.StoreSort;
import com.kkinikong.be.store.dto.response.*;
import com.kkinikong.be.store.exception.StoreException;
import com.kkinikong.be.store.exception.errorcode.StoreErrorCode;
import com.kkinikong.be.store.repository.store.StoreRepository;
import com.kkinikong.be.store.repository.storescrap.StoreScrapRepository;
import com.kkinikong.be.store.util.google.StoreGoogleApiClient;
import com.kkinikong.be.store.util.kakao.StoreKakaoApiClient;
import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.exception.UserException;
import com.kkinikong.be.user.exception.errorcode.UserErrorCode;
import com.kkinikong.be.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StoreService {

  private final StoreRepository storeRepository;
  private final StoreKakaoApiClient storeKakaoApiClient;
  private final StoreGoogleApiClient storeGoogleApiClient;
  private final StoreCacheService storeCacheService;
  private final ReviewTagRepository reviewTagRepository;
  private final StoreScrapRepository storeScrapRepository;
  private final UserRepository userRepository;

  private static final String NO_INFO = "NO_INFO";

  public PageResponse<StoreListItemResponse> getStoreListWithTag(
      Double latitude,
      Double longitude,
      Category category,
      StoreSort sort,
      int page,
      int size,
      Long userId) {
    Pageable pageable = PageRequest.of(page, size);

    Page<Store> storePage =
        storeRepository.findStoresForSorted(latitude, longitude, category, sort, pageable, userId);

    // 가맹점 ID 리스트 뽑아 태그 조회
    List<Long> storeIds = storePage.getContent().stream().map(Store::getId).toList();
    Map<Long, String> tagMap = reviewTagRepository.findRepresentativeTagByStoreId(storeIds);

    return PageResponse.from(
        storePage, store -> StoreListItemResponse.from(store, tagMap.get(store.getId())));
  }

  public PageResponse<StoreMapItemResponse> getStoreListWithMap(
      Double latitude, Double longitude, Category category, int page, int size, Long userId) {

    Pageable pageable = PageRequest.of(page, size);

    Page<Store> storePage =
        storeRepository.findStoresByDistanceOrName(latitude, longitude, category, pageable, userId);

    return PageResponse.from(storePage, StoreMapItemResponse::from);
  }

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

  @Cacheable(value = "store-ids", key = "#storeId", unless = "#result == null")
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

  @Transactional
  public StoreScrapResponse addScrap(Long storeId, Long userId) {
    User user =
        userRepository
            .findUserById(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    Store store =
        storeRepository
            .findById(storeId)
            .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));

    boolean alreadyExists =
        storeScrapRepository.findByStoreIdAndUserId(storeId, userId).isPresent();
    if (alreadyExists) {
      throw new StoreException(StoreErrorCode.ALREADY_SCRAPPED);
    }

    store.increaseScrapCount();
    storeScrapRepository.save(new StoreScrap(user, store));
    return StoreScrapResponse.of(true, store.getScrapCount());
  }

  @Transactional
  public StoreScrapResponse removeScrap(Long storeId, Long userId) {
    StoreScrap storeScrap =
        storeScrapRepository
            .findByStoreIdAndUserId(storeId, userId)
            .orElseThrow(() -> new StoreException(StoreErrorCode.SCRAP_NOT_FOUND));
    Store store = storeScrap.getStore();
    store.decreaseScrapCount();
    storeScrapRepository.delete(storeScrap);
    return StoreScrapResponse.of(false, store.getScrapCount());
  }
}
