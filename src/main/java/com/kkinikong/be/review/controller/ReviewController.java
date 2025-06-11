package com.kkinikong.be.review.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.review.dto.request.ReviewRequest;
import com.kkinikong.be.review.dto.response.ReviewListItemResponse;
import com.kkinikong.be.review.dto.response.ReviewPostResponse;
import com.kkinikong.be.review.service.ReviewService;
import com.kkinikong.be.user.utils.CustomUserDetails;

@Controller
@RequiredArgsConstructor
@Tag(name = "Review", description = "리뷰 관련 API")
@RequestMapping("/api/v1/{storeId}/review")
public class ReviewController {

  private final ReviewService reviewService;

  @Operation(
      summary = "가맹점 리뷰 작성하기",
      description =
          """
        - 가맹점의 리뷰는 로그인한 사용자만 작성할 수 있습니다.
        - 리뷰 작성 완료 후, 리뷰 id를 반환합니다.
        - 별점은 1~5 사이의 정수로 입력해야 합니다. (필수)
        - 태그는 최대 5개까지 선택할 수 있습니다. (선택)
        - 텍스트는 최대 500자까지 입력할 수 있습니다. (선택)
        """)
  @PostMapping
  public ResponseEntity<ApiResponse<Object>> postReview(
      @PathVariable("storeId") Long storeId,
      @RequestBody @Valid ReviewRequest reviewRequest,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    ReviewPostResponse reviewPostResponse =
        reviewService.postReview(storeId, reviewRequest, userDetails.getId());

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(reviewPostResponse));
  }

  @Operation(
      summary = "가맹점 리뷰 리스트 및 별점 조회하기",
      description =
          """
          - 가맹점의 리뷰 리스트를 조회합니다.
          - 리뷰는 최신순으로 가져옵니다.
          - 페이지 번호는 0부터 시작
          - 페이지 크기(size)는 기본 10개이며, 조정 가능
          - 로그인 한 경우에는 isOwner 필드가 true/false로 반환되고 로그인 하지 않은 경우에는 null로 반환됩니다.
          """)
  @GetMapping
  public ResponseEntity<ApiResponse<Object>> getReviewListAndRating(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @PathVariable("storeId") Long storeId,
      @Nullable @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = userDetails != null ? userDetails.getId() : null;

    ReviewListItemResponse reviewListAndRating =
        reviewService.getReviewListAndRating(storeId, page, size, userId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(reviewListAndRating));
  }

  @Operation(
      summary = "리뷰 작성 시 사진 추가",
      description =
          """
           - 리뷰 작성 api 호출을 통해 리뷰를 작성해 id를 받은 후, 해당 id로 사진을 추가합니다.
           - 사진이 없는 경우 file 비워서 보내셔도 되고 아예 호출 안하셔도 됩니다.
           - 사진은 1장만 전송 가능하며 최대 10MB까지 가능합니다.
           - 가능한 파일 확장자는 .jpg, .jpeg, .png, .heic 입니다.
           """)
  @PostMapping(path = "/{reviewId}/photo", consumes = "multipart/form-data")
  public ResponseEntity<ApiResponse<Object>> postReviewImage(
      @PathVariable("reviewId") Long reviewId,
      @Parameter(
              description = "업로드할 파일 리스트",
              content = @Content(mediaType = "application/octet-stream"))
          @RequestParam(value = "file", required = false)
          MultipartFile file,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    reviewService.postReviewImage(reviewId, file, userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }

  @Operation(
      summary = "리뷰 삭제하기",
      description = "리뷰는 작성자만 삭제할 수 있습니다. 리뷰 삭제 시, 해당 리뷰에 대한 사진도 함께 삭제됩니다.")
  @DeleteMapping("/{reviewId}")
  public ResponseEntity<ApiResponse<Object>> deleteReview(
      @PathVariable("storeId") Long storeId,
      @PathVariable("reviewId") Long reviewId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    reviewService.deleteReview(storeId, reviewId, userDetails.getId());

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }
}
