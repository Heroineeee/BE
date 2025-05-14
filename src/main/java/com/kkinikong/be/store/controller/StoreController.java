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

  @Operation(
      summary = "가맹점 찾기 화면 리스트 조회",
      description =
          """
  - GPS 설정을 하지 않았을 경우 : 기본 값인 인천 서구 중심 좌표 기준 5km 반경 가맹점 조회
  - GPS 설정을 하였을 경우 : 사용자의 위치를 기반으로 주변 반경 5km 의 가맹점 조회
  - 정렬 조건은 가까운 순(DISTANCE), 별점 높은 순(RATING), 리뷰 많은 순(REVIEW_COUNT), 조회수 순(VIEW_COUNT) 중 선택
  - category를 선택하지 않으면 전체 가맹점 조회
  - 필터링 결과가 동일할 경우, 이름 가나다순으로 정렬
  - 로그인한 유저는 isScrapped 가 true/false 로 반환
  - 로그인하지 않은 경우, isScrapped는 null로 응답
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
      @RequestParam StoreSort sort,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = (userDetails != null) ? userDetails.getId() : null;
    PageResponse<StoreListItemResponse> response =
        storeService.getStoreList(latitude, longitude, category, sort, page, size, userId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }

  @Operation(
      summary = "가맹점 지도 화면 리스트 조회",
      description =
          """
  - GPS 설정을 하지 않았을 경우 : 기본 값은 인천 서구 중심 좌표 기준 5km 반경 가맹점 조회
  - GPS 설정을 하였을 경우 : 사용자의 위치를 기반으로 주변 반경 5km 의 가맹점 조회
  - 가맹점은 무조건 가까운 순서대로 조회
  - category를 선택하지 않으면 전체 가맹점 조회
  - 로그인한 유저는 isScrapped 필드가 true/false로 반환
  - 로그인하지 않은 경우, isScrapped는 null로 반환
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
    PageResponse<StoreMapListItemResponse> response =
        storeService.getStoreListWithMap(latitude, longitude, category, page, size, userId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }

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

  @Operation(
      summary = "가맹점 찾기 화면 검색",
      description =
          """
  - GPS 설정을 하지 않았을 경우 : 기본 값은 인천 서구 중심 좌표 기준 5km 반경 가맹점 조회
  - GPS 설정을 하였을 경우 : 사용자의 위치를 기반으로 주변 반경 5km 의 가맹점 조회
  - 검색어를 입력하지 않으면 빈 리스트 반환
                  """)
  @GetMapping()
  public ResponseEntity<ApiResponse<Object>> findStoresList(
      @Parameter(description = "인천 서구의 임의의 위도 값", example = "37.545472")
          @RequestParam(required = false)
          Double latitude,
      @Parameter(description = "인천 서구의 임의의 경도 값", example = "126.676902")
          @RequestParam(required = false)
          Double longitude,
      @RequestParam(required = false) String keyword,
      @RequestParam StoreSort sort,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = (userDetails != null) ? userDetails.getId() : null;
    PageResponse<StoreListItemResponse> response =
        storeService.searchStoresForList(latitude, longitude, keyword, sort, page, size, userId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }

  @Operation(
      summary = "가맹점 지도 화면 & 메인페이지 검색",
      description =
          """
            - GPS 설정을 하지 않았을 경우 : 기본 값은 인천 서구 중심 좌표 기준 5km 반경 가맹점 조회
            - GPS 설정을 하였을 경우 : 사용자의 위치를 기반으로 주변 반경 5km 의 가맹점 조회
            - 검색어를 입력하지 않으면 빈 리스트 반환
                            """)
  @GetMapping("/map")
  public ResponseEntity<ApiResponse<Object>> findStoresMapList(
      @Parameter(description = "인천 서구의 임의의 위도 값", example = "37.545472")
          @RequestParam(required = false)
          Double latitude,
      @Parameter(description = "인천 서구의 임의의 경도 값", example = "126.676902")
          @RequestParam(required = false)
          Double longitude,
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = (userDetails != null) ? userDetails.getId() : null;
    PageResponse<StoreMapListItemResponse> response =
        storeService.searchStoresForMapList(latitude, longitude, keyword, page, size, userId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
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
