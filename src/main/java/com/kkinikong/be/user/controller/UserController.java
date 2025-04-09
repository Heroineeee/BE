package com.kkinikong.be.user.controller;

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

import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.user.dto.request.NicknameRequest;
import com.kkinikong.be.user.service.UserService;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "유저 API")
public class UserController {
  private final UserService userService;

  @Operation(summary = "닉네임 등록", description = "로그인 후 유저의 닉네임을 등록합니다.")
  @PostMapping("/nickname")
  public ResponseEntity<ApiResponse<?>> nickname(
      @Valid @RequestBody NicknameRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    userService.updateNickname(request, userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }
}
