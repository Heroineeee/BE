package com.kkinikong.be.convenience.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.convenience.domain.type.Brand;
import com.kkinikong.be.convenience.domain.type.Category;
import com.kkinikong.be.convenience.dto.request.ConveniencePostInfoRequest;
import com.kkinikong.be.convenience.dto.request.ConvenienceRequest;
import com.kkinikong.be.convenience.dto.response.ConveniencePostInfoResponse;
import com.kkinikong.be.convenience.dto.response.ConveniencePostListResponse;
import com.kkinikong.be.convenience.dto.response.ConveniencePostResponse;
import com.kkinikong.be.convenience.service.ConvenienceService;
import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.global.response.PageResponse;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@Tag(name = "Convenience", description = "편의점 관련 API")
@RequestMapping("/api/v1/convenience")
public class ConvenienceController {

  private final ConvenienceService convenienceService;

  @GetMapping("/recommendation")
  @Operation(
      summary = "정확한 제품명 추천",
      description = "편의점 제품명을 입력하면, 정확한 제품명을 추천해주는 API입니다. " + "입력한 제품명에 대한 추천 결과를 3개 반환합니다.")
  public ResponseEntity<ApiResponse<Object>> getProductNameRecommendation(
      @RequestParam String productName, @AuthenticationPrincipal CustomUserDetails userDetails) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.from(convenienceService.getProductNameRecommendation(productName)));
  }

  @Operation(
      summary = "편의점 정보 게시글 작성",
      description =
          """
                    - brand 는 GS25, CU, SEVEN_ELEVEN, EMART_24, MINI_STOP 중 하나를 선택합니다. (필수)
                    - 카테고리는 MEAL, SNACK, DRINK, FRUIT, ETC 중에 하나를 선택합니다. (필수)
                    - 상세 설명은 선택입니다. 공백 포함 최대 300자까지 입력 가능합니다.
                    """)
  @PostMapping("/post")
  public ResponseEntity<ApiResponse<Object>> postConveniencePost(
      @RequestBody @Valid ConvenienceRequest convenienceRequest,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    ConveniencePostResponse conveniencePostResponse =
        convenienceService.postConvenience(convenienceRequest, userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(conveniencePostResponse));
  }

  @Operation(summary = "편의점 정보 게시글 삭제", description = "편의점 정보 게시글은 작성자만 삭제할 수 있습니다.")
  @DeleteMapping("/post/{postId}")
  public ResponseEntity<ApiResponse<Object>> deleteConveniencePost(
      @PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
    convenienceService.deleteConveniencePost(postId, userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }

  @Operation(
      summary = "편의점 정보 게시글에 '올바른/잘못된 정보' 표시",
      description =
          """
          - `true` : 올바른 정보예요
          - `false` : 잘못된 정보예요
          - 사용자는 게시글당 한 번만 평가할 수 있으며, 기존 평가가 있는 경우 수정됩니다.
          """)
  @PostMapping("/post/{postId}/info")
  public ResponseEntity<ApiResponse<Object>> addConveniencePostInfo(
      @PathVariable("postId") Long postId,
      @RequestBody @Valid ConveniencePostInfoRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    ConveniencePostInfoResponse response =
        convenienceService.addConveniencePostInfo(postId, request.isCorrect(), userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }

  @Operation(
      summary = "편의점 정보 게시판 리스트 조회",
      description =
          """
          - 검색어(keyword), 카테고리(category), 브랜드(brand), 사용 가능 여부(isAvailable)로 필터링할 수 있습니다.
          - 모든 파라미터는 선택 사항이며, 기본값은 null입니다.
          - keyword : 제품이름으로 검색가능하며 글자수 제한 없습니다. 결과가 없을 경우 ~~ 반환합니다.
          - 카테고리와 브랜드는 최신순으로 정렬됩니다.
          - isAvailable : true인 경우 사용 가능한 제품만, false인 경우 사용 불가능한 제품만 조회합니다.
          """)
  @GetMapping("/list")
  public ResponseEntity<ApiResponse<Object>> getConveniencePostList(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Category category,
      @RequestParam(required = false) Brand brand,
      @RequestParam(required = false) boolean isAvailable,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    PageResponse<ConveniencePostListResponse> conveniencePostList =
        convenienceService.getConveniencePostList(
            keyword, category, brand, isAvailable, page, size);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(conveniencePostList));
  }
}
