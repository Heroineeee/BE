package com.kkinikong.be.opensearch.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.opensearch.service.OpenSearchService;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@Tag(
    name = "OpenSearch Admin",
    description = "OpenSearch 관리 API (관리자 전용, 프론트 연동 X), 유저의 권한이 USER인 경우에는 접근할 수 없습니다.")
@RequestMapping("/api/v1/admin/opensearch")
public class OpenSearchAdminController {
  private final OpenSearchService openSearchService;

  @PostMapping("/create-index")
  @Operation(
      summary = "OpenSearch 인덱스 생성",
      description = "OpenSearch 인덱스를 생성하는 API입니다. 인덱스가 이미 존재하는 경우에는 아무 작업도 수행하지 않습니다.")
  public ResponseEntity<ApiResponse<Object>> createIndex(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    openSearchService.createIndexIfNotExists();
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }
}
