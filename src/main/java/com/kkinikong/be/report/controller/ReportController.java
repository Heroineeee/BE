package com.kkinikong.be.report.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.report.domain.type.CommonReportReason;
import com.kkinikong.be.report.domain.type.StoreReportReason;
import com.kkinikong.be.report.dto.request.ReportRequest;
import com.kkinikong.be.report.service.ReportService;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
@Tag(name = "Report", description = "신고 관련 API")
@RestController
public class ReportController {

  private final ReportService reportService;

  @Operation(
      summary = "가맹점 정보 수정 요청",
      description =
          """
      - 가맹점 정보 수정 요청을 합니다.
      - reason은 "CATEGORY", "LOCATION", "BUSINESS_HOURS", "CLOSED", "ETC" 중에서 선택해주세요.
      - ETC를 선택한 경우, description을 입력해주세요.
      - description은 500자 이내로 작성해주세요.
      - ETC를 선택하지 않은 경우, description은 내용이 있더라도 null로 전달되며 requestBody를 비워서 보내도 됩니다.
      - STORE_REPORT_ALREAY_EXISTS : 이미 신고한 가게는 7일 이내에 다시 신고할 수 없습니다.
      """)
  @PostMapping("store/{storeId}")
  public ResponseEntity<ApiResponse<Object>> reportStore(
      @PathVariable("storeId") Long storeId,
      @RequestParam StoreReportReason reason,
      @RequestBody @Nullable @Valid ReportRequest reportRequest,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    reportService.reportStore(storeId, reason, reportRequest, userDetails.getId());

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }

  @Operation(
      summary = "리뷰 신고하기",
      description =
          """
      - reason은 "ABUSIVE_LANGUAGE", "FAKE_INFO", "SPAM", "CLOSED", "PRIVACY" 중에서 선택해주세요.
      - ETC를 선택한 경우, description을 입력해주세요.
      - description은 500자 이내로 작성해주세요.
      - ETC를 선택하지 않은 경우, description은 내용이 있더라도 null로 전달되며 requestBody를 비워서 보내도 됩니다.
      """)
  @PostMapping("review/{reviewId}")
  public ResponseEntity<ApiResponse<Object>> reportReview(
      @PathVariable("reviewId") Long reviewId,
      @RequestParam CommonReportReason reason,
      @RequestBody @Nullable @Valid ReportRequest reportRequest,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    reportService.reportReview(reviewId, reason, reportRequest, userDetails.getId());

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }

  @Operation(
      summary = "커뮤니티 게시글 신고하기",
      description =
          """
      - reason은 "ABUSIVE_LANGUAGE", "FAKE_INFO", "SPAM", "CLOSED", "PRIVACY" 중에서 선택해주세요.
      - ETC를 선택한 경우, description을 입력해주세요.
      - description은 500자 이내로 작성해주세요.
      - ETC를 선택하지 않은 경우, description은 내용이 있더라도 null로 전달되며 requestBody를 비워서 보내도 됩니다.
      """)
  @PostMapping("community/post/{postId}")
  public ResponseEntity<ApiResponse<Object>> reportCommunityPost(
      @PathVariable("postId") Long postId,
      @RequestParam CommonReportReason reason,
      @RequestBody @Nullable @Valid ReportRequest reportRequest,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    reportService.reportCommunityPost(postId, reason, reportRequest, userDetails.getId());

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }

  @Operation(
      summary = "커뮤니티 댓글 신고하기",
      description =
          """
      - reason은 "ABUSIVE_LANGUAGE", "FAKE_INFO", "SPAM", "CLOSED", "PRIVACY" 중에서 선택해주세요.
      - ETC를 선택한 경우, description을 입력해주세요.
      - description은 500자 이내로 작성해주세요.
      - ETC를 선택하지 않은 경우, description은 내용이 있더라도 null로 전달되며 requestBody를 비워서 보내도 됩니다.
      """)
  @PostMapping("community/comment/{commentId}")
  public ResponseEntity<ApiResponse<Object>> reportCommunity(
      @PathVariable("commentId") Long commentId,
      @RequestParam CommonReportReason reason,
      @RequestBody @Nullable @Valid ReportRequest reportRequest,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    reportService.reportComment(commentId, reason, reportRequest, userDetails.getId());

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }
}
