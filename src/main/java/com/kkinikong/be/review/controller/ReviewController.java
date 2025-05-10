package com.kkinikong.be.review.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.user.utils.CustomUserDetails;

@Controller
@RequiredArgsConstructor
@Tag(name = "Store", description = "가맹점 관련 API")
@RequestMapping("/api/v1/{storeId}/review")
public class ReviewController {

  @Operation(
      summary = "리뷰 작성하기",
      description =
          """
            - 리뷰 작성하기
            - 리뷰 작성 시, 가맹점의 id를 path variable로 전달해야 함
            - 리뷰 작성 시, 리뷰 내용과 평점을 body로 전달해야 함
            - 리뷰 작성 시, 로그인한 유저의 id를 header로 전달해야 함
            """)
  @PostMapping
  public ResponseEntity<ApiResponse<Object>> postReview(
      @PathVariable("storeId") Long storeId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }
}
