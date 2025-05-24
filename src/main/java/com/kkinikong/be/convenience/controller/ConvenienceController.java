package com.kkinikong.be.convenience.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@Tag(name = "Convenience", description = "편의점 관련 API")
@RequestMapping("/api/v1/convenience")
public class ConvenienceController {

  @GetMapping("/product-name/recommendation")
  @Operation(summary = "정확한 제품명 추천")
  public ResponseEntity<ApiResponse<Object>> getProductNameRecommendation(
      @RequestParam String productName, @AuthenticationPrincipal CustomUserDetails userDetails) {

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }
}
