package com.kkinikong.be.user.controller;

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
import com.kkinikong.be.global.response.PageResponse;
import com.kkinikong.be.store.dto.response.StoreMapListItemResponse;
import com.kkinikong.be.user.service.MypageService;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
@Tag(name = "Mypage", description = "마이페이지 관련 API")
@RestController
public class MypageController {
  private final MypageService mypageService;

  @Operation(summary = "내가 찜한 가게 조회")
  @GetMapping("/scrap")
  public ResponseEntity<ApiResponse<Object>> getScrapStoreList(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    PageResponse<StoreMapListItemResponse> response =
        mypageService.getScrapStore(userDetails.getId(), page, size);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }
}
