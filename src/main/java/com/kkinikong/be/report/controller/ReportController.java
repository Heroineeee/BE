package com.kkinikong.be.report.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.ApiResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
@Tag(name = "Report", description = "신고 관련 API")
@RestController
public class ReportController {

  @Operation(summary = "가맹점 정보 수정 요청", description = "가맹점 정보 수정 요청을 합니다.")
  @PostMapping("/{storeId}")
  public ResponseEntity<ApiResponse<Object>> reportStore(@PathVariable("storeId") Long storeId) {
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }
}
