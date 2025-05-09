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
import com.kkinikong.be.report.domain.type.StoreReportReason;
import com.kkinikong.be.report.dto.request.ReportStoreRequest;
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
      - 이미 신고한 가게인 경우, REPORT_ALREAY_EXISTS 400 에러를 반환합니다.
      """)
  @PostMapping("/{storeId}")
  public ResponseEntity<ApiResponse<Object>> reportStore(
      @PathVariable("storeId") Long storeId,
      @RequestParam StoreReportReason reason,
      @RequestBody @Nullable @Valid ReportStoreRequest reportStoreRequest,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    reportService.reportStore(storeId, reason, reportStoreRequest, userDetails.getId());

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }
}
