package com.kkinikong.be.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

  @GetMapping
  public ResponseEntity<ApiResponse<Object>> getStores(
      @Parameter(description = "인천 서구의 임의의 위도 값", example = "37.545472") @RequestParam
          double latitude,
      @Parameter(description = "인천 서구의 임의의 경도 값", example = "126.676902") @RequestParam
          double longitude,
      @Parameter(description = "카테고리 필터, 미입력 시 전체 가맹점 조회") @RequestParam(required = false)
          Category category,
      @Parameter(description = "정렬 조건, 기본값: DISTANCE") @RequestParam(defaultValue = "DISTANCE")
          StoreSort sort,
      @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "한 페이지에 가져올 가맹점 개수") @RequestParam(defaultValue = "10") int size) {

    StoreListResponse response =
        storeService.getStores(latitude, longitude, category, sort, page, size);
    return ResponseEntity.ok(ApiResponse.from(response));
  }
}
