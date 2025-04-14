package com.kkinikong.be.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.auth.dto.request.NicknameRequest;
import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.user.service.UserService;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "유저 관련 API")
@RestController
public class UserController {
  private final UserService userService;

  @Operation(summary = "닉네임 등록", description = "로그인 후 유저의 닉네임을 등록합니다.")
  @PostMapping("/nickname")
  public ResponseEntity<ApiResponse<Object>> nickname(
      @Valid @RequestBody NicknameRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    userService.updateNickname(request, userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }

  @Operation(summary = "닉네임 중복 확인", description = "닉네임이 중복되면 true를, 중복되지 않으면 false를 반환합니다.")
  @GetMapping("/check-nickname")
  public ResponseEntity<ApiResponse<Object>> checkNickname(@RequestParam String nickname) {

    boolean isDuplicated = userService.checkNickname(nickname);
    return ResponseEntity.ok(ApiResponse.from(isDuplicated));
  }
}
