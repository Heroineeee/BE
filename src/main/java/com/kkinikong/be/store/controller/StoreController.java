package com.kkinikong.be.store.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.global.response.PageResponse;
import com.kkinikong.be.store.domain.type.Category;
import com.kkinikong.be.store.domain.type.StoreSort;
import com.kkinikong.be.store.dto.response.*;
import com.kkinikong.be.store.service.StoreService;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@Tag(name = "Store", description = "가맹점 관련 API")
@RequestMapping("/api/v1/store")
public class StoreController {

  private final StoreService storeService;

  @Operation(summary = "메인페이지 조회수 높은 가맹점 8개 조회")
  @GetMapping("/top")
  public ResponseEntity<ApiResponse<Object>> getTopStore(
      @Parameter(description = "인천 서구의 임의의 위도 값", example = "37.545472")
          @RequestParam(required = false)
          Double latitude,
      @Parameter(description = "인천 서구의 임의의 경도 값", example = "126.676902")
          @RequestParam(required = false)
          Double longitude) {
    List<StoreCardResponse> response = storeService.getTopViewedStores(latitude, longitude);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }

  @Operation(
      summary = "메인페이지 가맹점 지역 개수 조회",
      description =
          """
                - 지역은 가맹점 region에서 추출하며, 서울, 인천 지역 제외한 가맹점 지역 개수를 조회합니다.
                """)
  @GetMapping("/region-count")
  public ResponseEntity<ApiResponse<Object>> getStoreRegionCount() {
    StoreRegionCountResponse response = storeService.getStoreRegionCount();
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }

  @Operation(
      summary = "가맹점 찾기 화면 리스트/검색 통합 조회",
      description =
          """
                - keyword가 없으면: GPS 또는 디폴트 위치(인천 서구) 기준 전체 가맹점 조회
                - keyword가 주소(00동, 00구 등)일 경우: 해당 위치 기준 반경 5km 가맹점 조회
                - keyword가 음식명 등 일반 키워드일 경우: 현재 위치 기준 가맹점명/주소에 포함된 가맹점 조회
                - category 필터 선택안하면 전체 가맹점 조회
                - 정렬: 거리순(DISTANCE), 별점순(RATING), 리뷰순(REVIEW_COUNT), 조회수순(VIEW_COUNT)
                """)
  @GetMapping("/list")
  public ResponseEntity<ApiResponse<Object>> getStoresList(
      @Parameter(description = "인천 서구의 임의의 위도 값", example = "37.545472")
          @RequestParam(required = false)
          Double latitude,
      @Parameter(description = "인천 서구의 임의의 경도 값", example = "126.676902")
          @RequestParam(required = false)
          Double longitude,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Category category,
      @RequestParam StoreSort sort,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = (userDetails != null) ? userDetails.getId() : null;
    PageResponse<StoreListItemResponse> response =
        storeService.getStoreList(latitude, longitude, keyword, category, sort, page, size, userId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }

  @Operation(
      summary = "가맹점 지도 화면 리스트/검색 통합 조회",
      description =
          """
                  - keyword가 없으면: GPS 또는 디폴트 위치(인천 서구) 기준 전체 가맹점 조회
                  - keyword가 주소(00동, 00구 등)일 경우: 해당 위치 기준 반경 5km 가맹점 조회
                  - keyword가 음식명 등 일반 키워드일 경우: 현재 위치 기준 가맹점명/주소에 포함된 가맹점 조회
                  - category 필터 선택안하면 전체 가맹점 조회
                  - radius를 통해 반경을 조절할 수 있다.
                """)
  @GetMapping("/map")
  public ResponseEntity<ApiResponse<Object>> getStoreMapList(
      @Parameter(description = "인천 서구의 임의의 위도 값", example = "37.545472")
          @RequestParam(required = false)
          Double latitude,
      @Parameter(description = "인천 서구의 임의의 경도 값", example = "126.676902")
          @RequestParam(required = false)
          Double longitude,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Double radius,
      @RequestParam(required = false) Category category,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = (userDetails != null) ? userDetails.getId() : null;
    PageResponse<StoreMapListItemResponse> response =
        storeService.getStoreMapList(
            latitude, longitude, radius, keyword, category, page, size, userId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }

  @Operation(
      summary = "가맹점 상세 정보 조회",
      description =
          """
                - 가맹점 ID를 통해 가맹점 정보를 조회합니다.
                - 가맹점 ID, 가맹점 카테고리, 가맹점 이름, 가맹점 주소, 영업시간, 스크랩 수, 업데이트 일자, 리뷰 수, 별점, 스크랩 여부를 포함합니다.
                - 로그인 한 경우에는 스크랩 여부가 true/false로 반환되고 로그인 하지 않은 경우에는 null로 반환됩니다.
                - 가맹점 영업시간 정보는 구글 API 호출을 통해 조회하고 캐싱되어 30일간 유지됩니다.
                - 영업시간 정보가 없는 경우, 영업시간 리스트에서 휴무일인 경우에 null로 반환됩니다.
                - 가맹점이 ID로 조회되지 않는 경우에는 STORE_NOT_FOUND 400 에러를 반환합니다.
                """)
  @GetMapping("/{storeId}")
  public ResponseEntity<ApiResponse<Object>> getStoreInfo(
      @PathVariable("storeId") Long storeId,
      @Nullable @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = (userDetails != null) ? userDetails.getId() : null;
    StoreInfoResponse storeInfo = storeService.getStoreInfo(storeId, userId);
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

  @Operation(summary = "가맹점 스크랩")
  @PostMapping("/scrap/{storeId}")
  public ResponseEntity<ApiResponse<Object>> scrapPost(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable("storeId") Long storeId) {
    StoreScrapResponse response = storeService.addScrap(storeId, userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }

  @Operation(summary = "가맹점 스크랩 취소")
  @DeleteMapping("/scrap/{storeId}")
  public ResponseEntity<ApiResponse<Object>> scrapDelete(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable("storeId") Long storeId) {
    StoreScrapResponse response = storeService.removeScrap(storeId, userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }
}
