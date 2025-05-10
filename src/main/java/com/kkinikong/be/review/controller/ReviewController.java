package com.kkinikong.be.review.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.review.dto.request.ReviewRequest;
import com.kkinikong.be.review.service.ReviewService;
import com.kkinikong.be.user.utils.CustomUserDetails;

@Controller
@RequiredArgsConstructor
@Tag(name = "Store", description = "가맹점 관련 API")
@RequestMapping("/api/v1/{storeId}/review")
public class ReviewController {

  private final ReviewService reviewService;

  @Operation(
      summary = "가맹점 리뷰 작성하기",
      description = "가맹점의 리뷰를 작성합니다. 로그인한 사용자만 작성할 수 있습니다. 리뷰 작성 완료 후, 리뷰 id를 반환합니다.")
  @PostMapping
  public ResponseEntity<ApiResponse<Object>> postReview(
      @PathVariable("storeId") Long storeId,
      @RequestBody @Valid ReviewRequest reviewRequest,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long reviewId = reviewService.postReview(storeId, reviewRequest, userDetails.getId());

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(reviewId));
  }

  @Operation(
      summary = "가맹점 리뷰 리스트 및 별점 조회하기",
      description =
          """
              - 리뷰 리스트 및 별점 조회하기
              - 리뷰 리스트 및 별점 조회 시, 가맹점의 id를 path variable로 전달해야 함
              - 리뷰 리스트 및 별점 조회 시, 로그인한 유저의 id를 header로 전달해야 함
              """)
  @GetMapping
  public ResponseEntity<ApiResponse<Object>> getReviewListAndRating(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @PathVariable("storeId") Long storeId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }

  @Operation(
      summary = "리뷰 작성 시 사진 추가",
      description =
          """
              - 리뷰 작성 시, 가맹점의 id를 path variable로 전달해야 함
              - 리뷰 작성 시, 사진을 body로 전달해야 함
              - 리뷰 작성 시, 로그인한 유저의 id를 header로 전달해야 함
              """)
  @PostMapping("/{reviewId}/photo")
  public ResponseEntity<ApiResponse<Object>> postReviewPhoto(
      @PathVariable("storeId") Long storeId,
      @PathVariable("reviewId") Long reviewId,
      @Parameter(
              description = "업로드할 파일 리스트",
              content = @Content(mediaType = "application/octet-stream"))
          @RequestParam(value = "file", required = false)
          List<MultipartFile> files,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }
}
