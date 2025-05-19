package com.kkinikong.be.auth.dto.response.naver;

public record NaverUserInfoResponse(NaverResponse response) {

  public record NaverResponse(String id, String email) {}
}
