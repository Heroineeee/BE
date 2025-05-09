package com.kkinikong.be.store.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.store.dto.response.StoreExternalLinkResponse;
import com.kkinikong.be.store.dto.response.StoreInfoResponse;
import com.kkinikong.be.store.service.StoreService;

@RequiredArgsConstructor
@RequestMapping("/api/v1/store")
@Tag(name = "Store", description = "가맹점 관련 API")
@RestController
public class StoreController {

  private final StoreService storeService;

  @Operation(
      summary = "가맹점 상세 정보 조회",
      description =
          """
          - 가맹점 ID를 통해 가맹점 정보를 조회합니다.
          - 가맹점 ID, 가맹점 카테고리, 가맹점 이름, 가맹점 주소, 영업시간, 스크랩 수, 업데이트 일자, 리뷰 수, 별점을 포함합니다.
          - 가맹점 영업시간 정보는 구글 API 호출을 통해 조회하고 캐싱되어 30일간 유지됩니다.
          - 영업시간 정보가 없는 경우, 영업시간 리스트에서 휴무일인 경우에 null로 반환됩니다.
          - 가맹점이 ID로 조회되지 않는 경우에는 STORE_NOT_FOUND 400 에러를 반환합니다.
          """)
  @GetMapping("/{storeId}")
  public ResponseEntity<ApiResponse<Object>> getStoreInfo(@PathVariable("storeId") Long storeId) {
    StoreInfoResponse storeInfo = storeService.getStoreInfo(storeId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(storeInfo));
  }

  @Operation(
      summary = "가맹점 메뉴 보러가기 및 길찾기",
      description =
          """
          - 가맹점 ID를 통해 가맹점 메뉴 보러가기 및 길찾기 링크를 조회합니다.
          - 카카오 API를 통해 가맹점 ID를 조회하고, 해당 ID를 통해 메뉴 보러가기 및 길찾기 링크를 생성합니다.
          - 카카오에 등록된 가맹점이 없는 경우에는 네이버 검색 링크로 대체합니다.
          - 저장된 외부링크는 API 호출을 줄이기 위해 캐싱되어 30일간 유지됩니다.
          - 가맹점이 ID로 조회되지 않는 경우에는 STORE_NOT_FOUND 400 에러를 반환합니다.
          """)
  @GetMapping("/{storeId}/external-links")
  public ResponseEntity<ApiResponse<Object>> getStoreExternalLink(
      @PathVariable("storeId") Long storeId) {
    StoreExternalLinkResponse storeExternalLink = storeService.getStoreExternalLink(storeId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(storeExternalLink));
  }
}
