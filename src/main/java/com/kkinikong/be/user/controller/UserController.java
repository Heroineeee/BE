package com.kkinikong.be.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.user.dto.request.NicknameRequest;
import com.kkinikong.be.user.dto.request.UserPlaceRequest;
import com.kkinikong.be.user.dto.response.NicknameResponse;
import com.kkinikong.be.user.dto.response.UserPlaceResponse;
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

    NicknameResponse response = userService.updateNickname(request, userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }

  @Operation(summary = "닉네임 중복 확인", description = "닉네임이 중복되면 true를, 중복되지 않으면 false를 반환합니다.")
  @GetMapping("/check-nickname")
  public ResponseEntity<ApiResponse<Object>> checkNickname(@RequestParam String nickname) {

    boolean isDuplicated = userService.checkNickname(nickname);
    return ResponseEntity.ok(ApiResponse.from(isDuplicated));
  }

  @Operation(summary = "유저가 자주 가는 지역 설정")
  @PostMapping("/place")
  public ResponseEntity<ApiResponse<Object>> userPlace(
      @RequestBody @Valid UserPlaceRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    userService.setUserPlace(userDetails.getId(), request.latitude(), request.longitude());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }

  @Operation(summary = "유저가 자주가는 지역 위경도 불러오기")
  @GetMapping("/place")
  public ResponseEntity<ApiResponse<Object>> userPlace(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    UserPlaceResponse response = userService.getUserPlace(userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }

  @Operation(summary = "회원 탈퇴")
  @DeleteMapping("/me")
  public ResponseEntity<ApiResponse<Object>> withdraw(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    userService.deleteUser(userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }

  @Operation(summary = "로그인한 유저의 닉네임 조회")
  @GetMapping("/nickname")
  public ResponseEntity<ApiResponse<Object>> getNickname(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    NicknameResponse response = userService.getNickname(userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(response));
  }
}
