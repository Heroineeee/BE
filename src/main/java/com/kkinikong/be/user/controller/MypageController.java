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

import com.kkinikong.be.community.dto.response.CommunityPostListResponse;
import com.kkinikong.be.convenience.dto.response.ConveniencePostListResponse;
import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.global.response.PageResponse;
import com.kkinikong.be.user.dto.response.MypageReviewResponse;
import com.kkinikong.be.user.dto.response.MypageStoreResponse;
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
    PageResponse<MypageStoreResponse> response =
        mypageService.getScrapStore(userDetails.getId(), page, size);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }

  @Operation(summary = "내가 작성한 리뷰 조회")
  @GetMapping("/review")
  public ResponseEntity<ApiResponse<Object>> getReviewList(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    PageResponse<MypageReviewResponse> response =
        mypageService.getReview(userDetails.getId(), page, size);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }

  @Operation(summary = "내가 작성한 편의점 정보 게시판 게시글 조회")
  @GetMapping("/convenience")
  public ResponseEntity<ApiResponse<Object>> getConvenienceList(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    PageResponse<ConveniencePostListResponse> response =
        mypageService.getConveniencePost(userDetails.getId(), page, size);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }

  @Operation(summary = "내가 작성한 커뮤니티 게시글 조회")
  @GetMapping("/community")
  public ResponseEntity<ApiResponse<Object>> getCommunityList(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    PageResponse<CommunityPostListResponse> response =
        mypageService.getCommunityPost(userDetails.getId(), page, size);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }
}
