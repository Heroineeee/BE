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
import com.kkinikong.be.store.dto.response.StoreListResponse;
import com.kkinikong.be.store.service.StoreService;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@Tag(name = "Store", description = "가맹점 관련 API")
@RequestMapping("/api/v1/store")
public class StoreController {

  private final StoreService storeService;

  @Operation(
      summary = "가맹점 찾기 리스트 조회",
      description =
          """
        현재 위치 기반으로 가맹점 목록을 조회합니다. 페이지 번호는 0부터 시작하며, 페이지 당 10개의 가맹점을 반환합니다.
        - category를 선택하지 않으면 전체 가맹점 조회
        - 정렬 조건은 가까운 순(DISTANCE), 별점 높은 순(RATING), 리뷰 많은 순(REVIEW_COUNT), 조회수 순(VIEW_COUNT) 중 선택
        - 필터링 결과가 동일할 경우, 이름 가나다순으로 정렬
        - 로그인한 유저는 isScrapped 가 true/false 로 반환
        - 로그인하지 않은 경우, isScrapped는 null로 응답
        """)
  @GetMapping
  public ResponseEntity<ApiResponse<Object>> getStoresList(
      @Parameter(description = "인천 서구의 임의의 위도 값", example = "37.545472") @RequestParam
          double latitude,
      @Parameter(description = "인천 서구의 임의의 경도 값", example = "126.676902") @RequestParam
          double longitude,
      @RequestParam(required = false) Category category,
      @RequestParam(defaultValue = "VIEW_COUNT") StoreSort sort,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long userId = (userDetails != null) ? userDetails.getId() : null;

    StoreListResponse response =
        storeService.getStores(latitude, longitude, category, sort, page, size, userId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }
}
