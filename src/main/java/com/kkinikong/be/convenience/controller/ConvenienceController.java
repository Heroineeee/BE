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
import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RequiredArgsConstructor
@RequestMapping("/api/v1/convenience")
@Tag(name = "Convenience", description = "편의점 관련 API")
@RestController
public class ConvenienceController {

  private final ConvenienceService convenienceService;

  @Operation(summary = "편의점 정보 게시글 작성")
  @PostMapping("/post")
  public ResponseEntity<ApiResponse<Object>> postConvenience(
      @RequestBody @Valid ConvenienceRequest convenienceRequest,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    ConveniencePostResponse conveniencePostResponse =
        convenienceService.postConvenience(convenienceRequest, userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(conveniencePostResponse));
  }
}
