package com.kkinimong.be.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.kkinimong.be.auth.dto.request.BasicLoginRequest;
import com.kkinimong.be.auth.dto.response.LoginResponse;
import com.kkinimong.be.auth.service.AuthService;
import com.kkinimong.be.global.response.ApiResponse;
import com.kkinimong.be.user.domain.type.LoginType;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "인증 및 회원 관련 API")
@RestController
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "소셜 로그인 / 회원가입", description = "소셜 로그인을 진행합니다. (카카오) 인가코드를 넣어주세요.")
  @GetMapping("/login/{loginType}")
  public ResponseEntity<ApiResponse<Object>> login(
      @PathVariable LoginType loginType, @RequestParam String code) {

    LoginResponse loginResponse = authService.socialLogin(loginType, code);

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(loginResponse));
  }

  @Operation(
      summary = "기본 회원가입",
      description = "(관리자) 이메일과 비밀번호로 회원가입합니다. '@'를 포함한 이메일과 비밀번호를 입력해주세요.")
  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<Object>> signUp(
      @Valid @RequestBody BasicLoginRequest basicLoginRequest) {

    authService.signup(basicLoginRequest);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }

  @Operation(
      summary = "기본 로그인",
      description = "(관리자) 이메일과 비밀번호로 로그인합니다. '@'를 포함한 이메일과 비밀번호를 입력해주세요.")
  @PostMapping("/login")
  public ResponseEntity<ApiResponse<Object>> login(
      @Valid @RequestBody BasicLoginRequest basicLoginRequest) {

    LoginResponse loginResponse = authService.login(basicLoginRequest);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(loginResponse));
  }
}
