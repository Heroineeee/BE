package com.utopia.utopia_be.auth.dto.response;

public record KakaoUserInfoResponse(Long id, KakaoAccount kakaoAccount) {
  public record KakaoAccount(String email) {}
}
