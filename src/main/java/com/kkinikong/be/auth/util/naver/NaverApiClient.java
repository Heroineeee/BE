package com.kkinikong.be.auth.util.naver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.auth.dto.response.naver.NaverLoginResponse;
import com.kkinikong.be.auth.dto.response.naver.NaverUserInfoResponse;

@RequiredArgsConstructor
@Component
public class NaverApiClient {
  private final WebClient webClient;
  private static final String TOKEN_REQUEST_URI = "https://nid.naver.com/oauth2.0/token";
  private static final String USER_INFO_URI = "https://openapi.naver.com/v1/nid/me";

  @Value("${NAVER_CLIENT_ID}")
  private String naverApiKey;

  @Value("${NAVER_REDIRECT_URI}")
  private String naverRedirectUri;

  @Value("${NAVER_CLIENT_SECRET}")
  private String naverClientSecret;

  public String getAccessToken(String code) {
    return webClient
        .post()
        .uri(TOKEN_REQUEST_URI)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .bodyValue(
            "grant_type=authorization_code"
                + "&client_id="
                + naverApiKey
                + "&client_secret="
                + naverClientSecret
                + "&redirect_uri="
                + naverRedirectUri
                + "&code="
                + code)
        .retrieve()
        .bodyToMono(NaverLoginResponse.class)
        .map(NaverLoginResponse::accessToken)
        .block();
  }

  public NaverUserInfoResponse getUserInfo(String accessToken) {
    return webClient
        .get()
        .uri(USER_INFO_URI)
        .header("Authorization", "Bearer " + accessToken)
        .retrieve()
        .bodyToMono(NaverUserInfoResponse.class)
        .block();
  }
}
