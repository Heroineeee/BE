package com.kkinikong.be.auth.dto.response;

public record KakaoUserInfoResponse(Long id, KakaoAccount kakao_account) {
  public record KakaoAccount(String email) {}
}
