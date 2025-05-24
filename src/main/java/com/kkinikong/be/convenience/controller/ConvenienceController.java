package com.kkinikong.be.convenience.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.convenience.dto.request.ConvenienceRequest;
import com.kkinikong.be.convenience.dto.response.ConveniencePostResponse;
import com.kkinikong.be.convenience.service.ConvenienceService;
import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RequiredArgsConstructor
@RequestMapping("/api/v1/convenience")
@Tag(name = "Convenience", description = "편의점 관련 API")
@RestController
public class ConvenienceController {

  private final ConvenienceService convenienceService;

  @Operation(
      summary = "편의점 정보 게시글 작성",
      description =
          """
          - brand 는 GS25, CU, SEVENELEVEN, EMART24, MINISTOP 중 하나를 선택합니다.
          - 카테고리는 MEAL, SNACK, DRINK, FRUIT, ETC 중에 하나를 선택합니다.
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
  @DeleteMapping("/post/{conveniencePostId}")
  public ResponseEntity<ApiResponse<Object>> deleteConveniencePost(
      @PathVariable("conveniencePostId") Long conveniencePostId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    convenienceService.deleteConveniencePost(conveniencePostId, userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }
}
