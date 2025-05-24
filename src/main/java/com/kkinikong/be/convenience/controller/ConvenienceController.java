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

import com.kkinikong.be.convenience.dto.request.ConvenienceRequest;
import com.kkinikong.be.convenience.dto.response.ConveniencePostResponse;
import com.kkinikong.be.convenience.service.ConvenienceService;
import com.kkinikong.be.global.response.ApiResponse;
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

  @Operation(summary = "편의점 정보 게시글에 '올바른/잘못된 정보' 표시")
  @PostMapping("/post/{postId}/info")
  public ResponseEntity<ApiResponse<Object>> addConveniencePostInfo(
      @PathVariable("postId") Long postId,
      @RequestBody @Valid ConveniencePostInfoRequet request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    convenienceService.addConveniencePostInfo(postId, request.getIsCorrect(), userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }
}
