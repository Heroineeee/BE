package com.kkinikong.be.cache.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.cache.service.CacheService;
import com.kkinikong.be.cache.service.StoreCacheService;
import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RequiredArgsConstructor
@RequestMapping("/api/v1/cache")
@Tag(name = "Cache", description = "캐시 삭제 관련 API (관리자 전용, 프론트 연동 X) 유저의 권한이 USER인 경우에는 삭제할 수 없습니다.")
@RestController
public class CacheController {

  private final CacheService cacheService;
  private final StoreCacheService storeCacheService;

  @Operation(summary = "가맹점 외부 링크 캐시 초기화", description = "카카오 api를 통해 받은 모든 가맹점 외부 링크 캐시를 삭제합니다")
  @DeleteMapping("/store-id")
  public ResponseEntity<ApiResponse<Object>> clearStoredStoreIdCache(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    return ResponseEntity.ok(ApiResponse.from(cacheService.clearStoredStoreIdCache()));
  }

  @Operation(summary = "가맹점 영업 시간 캐시 초기화", description = "Google api를 통해 받은 모든 가맹점 영업 시간 캐시를 삭제합니다")
  @DeleteMapping("/opening-hours")
  public ResponseEntity<ApiResponse<Object>> clearStoredOpeningHoursCache(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    return ResponseEntity.ok(ApiResponse.from(cacheService.clearStoredOpeningHoursCache()));
  }

  @Operation(
      summary = "가맹점 위치 데이터 강제 동기화",
      description = "MySQL의 모든 가맹점 위치 정보를 Redis Geo Index로 적재합니다.")
  @PostMapping("/store-locations")
  public ResponseEntity<ApiResponse<Object>> syncStoreLocations(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    ;
    return ResponseEntity.ok(ApiResponse.from(storeCacheService.syncStoreLocations()));
  }
}
