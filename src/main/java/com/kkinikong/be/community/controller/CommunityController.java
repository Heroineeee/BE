package com.kkinikong.be.community.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.community.dto.request.CommunityPostRequest;
import com.kkinikong.be.community.dto.response.CommunityPostResponse;
import com.kkinikong.be.community.service.CommunityService;
import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@Tag(name = "Community", description = "커뮤니티 관련 API")
@RequestMapping("/api/v1/community")
public class CommunityController {

  CommunityService communityService;

  @PostMapping("/post")
  @Operation(summary = "커뮤니티 게시글 작성", description = "커뮤니티 게시글을 작성하는 API입니다. 모든 필드는 필수입니다")
  public ResponseEntity<ApiResponse<Object>> postCommunityPost(
      @RequestBody @Valid CommunityPostRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    CommunityPostResponse communityPostResponse =
        communityService.postCommunityPost(request, userDetails.getId());

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(communityPostResponse));
  }
}
