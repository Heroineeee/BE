package com.utopia.utopia_be.auth.dto.response;

public record KakaoUserInfoResponse(Long id, KakaoAccount kakao_account) {
  public record KakaoAccount(String email) {}
}
