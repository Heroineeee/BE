package com.kkinikong.be.store.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.store.dto.response.StoreExternalLinkResponse;
import com.kkinikong.be.store.dto.response.StoreInfoResponse;
import com.kkinikong.be.store.service.StoreCacheService;
import com.kkinikong.be.store.service.StoreService;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RequiredArgsConstructor
@RequestMapping("/api/v1/store")
@Tag(name = "Store", description = "가맹점 관련 API")
@RestController
public class StoreController {

  private final StoreService storeService;
  private final StoreCacheService storeCacheService;

  @Operation(summary = "가맹점 상세 정보 조회", description = "가맹점 상세 정보를 조회합니다.")
  @GetMapping("/{storeId}")
  public ResponseEntity<ApiResponse<Object>> getStoreInfo(@PathVariable("storeId") Long storeId) {
    StoreInfoResponse storeInfo = storeService.getStoreInfo(storeId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(storeInfo));
  }

  @Operation(summary = "가맹점 메뉴 보러가기 및 길찾기", description = "가맹점 메뉴 보러가기 및 길찾기 링크를 제공합니다.")
  @GetMapping("/{storeId}/external-links")
  public ResponseEntity<ApiResponse<Object>> getStoreExternalLink(
      @PathVariable("storeId") Long storeId) {
    StoreExternalLinkResponse storeExternalLink = storeService.getStoreExternalLink(storeId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(storeExternalLink));
  }

  @Operation(summary = "가맹점 정보 수정 요청", description = "가맹점 정보 수정 요청을 합니다.")
  @PostMapping("/{storeId}/report")
  public ResponseEntity<ApiResponse<Object>> postStoreReport(
      @PathVariable("storeId") Long storeId) {
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }

  @Operation(summary = "카카오 캐시 초기화", description = "모든 가맹점의 카카오 ID 캐시를 삭제합니다.")
  @DeleteMapping("/cache/kakao-id")
  public ResponseEntity<ApiResponse<Object>> clearKakaoCache(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    return ResponseEntity.ok(
        ApiResponse.from(storeCacheService.clearAllStoredKakaoId(userDetails.getId())));
  }
}
