package com.kkinikong.be.auth.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.auth.dto.request.BasicLoginRequest;
import com.kkinikong.be.auth.service.AuthService;
import com.kkinikong.be.global.response.ApiResponse;

@Profile({"local"})
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth 로컬", description = "인증 및 회원 관련 API")
@RestController
@RequiredArgsConstructor
public class SignupController {

  private final AuthService authService;

  @Operation(
      summary = "기본 회원가입",
      description = "(관리자) 이메일과 비밀번호로 회원가입합니다. '@'를 포함한 이메일과 비밀번호를 입력해주세요.")
  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<Object>> signUp(
      @Valid @RequestBody BasicLoginRequest basicLoginRequest) {

    authService.signup(basicLoginRequest);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }
}
