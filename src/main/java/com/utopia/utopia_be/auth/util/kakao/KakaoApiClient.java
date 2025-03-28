package com.utopia.utopia_be.auth.util.kakao;

import static com.utopia.utopia_be.auth.exception.errorcode.AuthErrorCode.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;

import com.utopia.utopia_be.auth.dto.response.KakaoLoginResponse;
import com.utopia.utopia_be.auth.dto.response.KakaoUserInfoResponse;
import com.utopia.utopia_be.auth.exception.AuthException;

@RequiredArgsConstructor
@Component
public class KakaoApiClient {
  private final WebClient webClient;
  private static final String USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";
  private static final String TOKEN_REQUEST_URI = "https://kauth.kakao.com/oauth/token";

  @Value("${KAKAO_REST_API_KEY}")
  private String kakaoApiKey;

  @Value("${KAKAO_REDIRECT_URI}")
  private String kakaoRedirectUri;

  // 인가 코드 > Access Token
  public String getAccessToken(String code) {
    try {
      KakaoLoginResponse response =
          webClient
              .post()
              .uri(TOKEN_REQUEST_URI)
              .header("Content-Type", "application/x-www-form-urlencoded")
              .bodyValue(
                  "grant_type=authorization_code&client_id="
                      + kakaoApiKey
                      + "&redirect_uri="
                      + kakaoRedirectUri
                      + "&code="
                      + code)
              .retrieve()
              .bodyToMono(KakaoLoginResponse.class)
              .block();

      return response.accessToken();

    } catch (WebClientResponseException e) {
      throw new AuthException(LOGIN_KAKAO_TOKEN_FAILED);
    }
  }

  // Access Token > 사용자 정보 조회
  public KakaoUserInfoResponse getUserInfo(String token) {
    try {
      return webClient
          .get()
          .uri(USER_INFO_URI)
          .header("Authorization", "Bearer " + token)
          .retrieve()
          .bodyToMono(KakaoUserInfoResponse.class)
          .block();
    } catch (WebClientResponseException e) {
      throw new AuthException(LOGIN_KAKAO_USERINFO_FAILED);
    }
  }
}
