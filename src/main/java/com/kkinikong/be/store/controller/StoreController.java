package com.kkinikong.be.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.store.domain.type.Category;
import com.kkinikong.be.store.domain.type.StoreSort;
import com.kkinikong.be.store.dto.response.StoreListResponse;
import com.kkinikong.be.store.service.StoreService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Store", description = "가맹점 관련 API")
@RequestMapping("/api/v1/store")
public class StoreController {

  private final StoreService storeService;

  @Operation(
      summary = "가맹점 리스트 조회",
      description =
          """
        현재 위치 기반으로 가맹점 목록을 조회합니다.
        - 카테고리, 정렬 조건, 페이징을 적용 가능
        - 위도(latitude), 경도(longitude)는 필수 입력
        - category를 입력하지 않으면 전체 가맹점 조회
        - 정렬 조건은 가까운 순(DISTANCE), 별점 높은 순(RATING), 리뷰 많은 순(REVIEW_COUNT), 조회수 순(VIEW_COUNT) 중 선택 가능
        - 필터링 결과가 동일할 경우, 이름 가나다순으로 정렬
        """)
  @GetMapping
  public ResponseEntity<ApiResponse<Object>> getStores(
      @Parameter(description = "인천 서구의 임의의 위도 값", example = "37.545472") @RequestParam
          double latitude,
      @Parameter(description = "인천 서구의 임의의 경도 값", example = "126.676902") @RequestParam
          double longitude,
      @RequestParam(required = false) Category category,
      @RequestParam(defaultValue = "DISTANCE") StoreSort sort,
      @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "한 페이지에 가져올 가맹점 개수") @RequestParam(defaultValue = "10") int size) {

    StoreListResponse response =
        storeService.getStores(latitude, longitude, category, sort, page, size);
    return ResponseEntity.ok(ApiResponse.from(response));
  }
}
