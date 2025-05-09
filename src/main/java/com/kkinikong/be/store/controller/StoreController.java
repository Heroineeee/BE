package com.kkinikong.be.store.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.store.domain.type.Category;
import com.kkinikong.be.store.domain.type.StoreSort;
import com.kkinikong.be.store.dto.response.PageResponse;
import com.kkinikong.be.store.dto.response.StoreListItemResponse;
import com.kkinikong.be.store.dto.response.StoreMapItemResponse;
import com.kkinikong.be.store.exception.StoreException;
import com.kkinikong.be.store.exception.errorcode.StoreErrorCode;
import com.kkinikong.be.store.service.StoreService;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@Tag(name = "Store", description = "가맹점 관련 API")
@RequestMapping("/api/v1/store")
public class StoreController {

  private final StoreService storeService;

  @Operation(
      summary = "가맹점 찾기 화면 리스트 조회",
      description =
          """
  - 정렬 조건은 가까운 순(DISTANCE), 별점 높은 순(RATING), 리뷰 많은 순(REVIEW_COUNT), 조회수 순(VIEW_COUNT) 중 선택
  - category를 선택하지 않으면 전체 가맹점 조회
  - 필터링 결과가 동일할 경우, 이름 가나다순으로 정렬
  - 정렬 조건이 DISTANCE이고 위도/경도가 없을 경우, 요청은 예외 처리됨
  - 로그인한 유저는 isScrapped 가 true/false 로 반환
  - 로그인하지 않은 경우, isScrapped는 null로 응답
  - 페이지 번호는 0부터 시작
  - 페이지 크기(size)는 기본 10개이며, 조정 가능
                  """)
  @GetMapping("/list")
  public ResponseEntity<ApiResponse<Object>> getStoresList(
      @Parameter(description = "인천 서구의 임의의 위도 값", example = "37.545472")
          @RequestParam(required = false)
          Double latitude,
      @Parameter(description = "인천 서구의 임의의 경도 값", example = "126.676902")
          @RequestParam(required = false)
          Double longitude,
      @RequestParam(required = false) Category category,
      @RequestParam(defaultValue = "VIEW_COUNT") StoreSort sort,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    if (sort == StoreSort.DISTANCE && (latitude == null || longitude == null)) {
      throw new StoreException(StoreErrorCode.MISSING_GPS_FOR_DISTANCE);
    }
    Long userId = (userDetails != null) ? userDetails.getId() : null;
    PageResponse<StoreListItemResponse> response =
        storeService.getStoreListWithTag(latitude, longitude, category, sort, page, size, userId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }

  @Operation(
      summary = "가맹점 지도 화면 리스트 조회",
      description =
          """
  - GPS를 포함하면 가까운 순으로 자동 정렬
  - GPS가 없으면 category 기준으로 이름 가나다순 정렬
  - category를 선택하지 않으면 전체 가맹점 조회
  - 로그인한 유저는 isScrapped 필드가 true/false로 반환
  - 로그인하지 않은 경우, isScrapped는 null로 반환
  - 페이지 번호는 0부터 시작
  - 페이지 크기(size)는 기본 10개이며, 조정 가능
""")
  @GetMapping("/list/map")
  public ResponseEntity<ApiResponse<Object>> getStoresMapList(
      @Parameter(description = "인천 서구의 임의의 위도 값", example = "37.545472")
          @RequestParam(required = false)
          Double latitude,
      @Parameter(description = "인천 서구의 임의의 경도 값", example = "126.676902")
          @RequestParam(required = false)
          Double longitude,
      @RequestParam(required = false) Category category,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = (userDetails != null) ? userDetails.getId() : null;
    PageResponse<StoreMapItemResponse> response =
        storeService.getStoreListWithMap(latitude, longitude, category, page, size, userId);
    return ResponseEntity.ok(ApiResponse.from(response));
  }

  @Operation(summary = "가맹점 스크랩")
  @PostMapping("/{storeId}/scrap")
  public ResponseEntity<ApiResponse<Object>> scrapPost(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable("storeId") Long storeId) {
    storeService.addScrap(storeId, userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(ApiResponse.EMPTY_RESPONSE));
  }

  @Operation(summary = "가맹점 스크랩 취소")
  @DeleteMapping("/{storeId}/scrap")
  public ResponseEntity<ApiResponse<Object>> scrapDelete(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable("storeId") Long storeId) {
    storeService.removeScrap(storeId, userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(ApiResponse.EMPTY_RESPONSE));
  }
}
