package com.utopia.utopia_be.auth.service;

import static com.utopia.utopia_be.auth.exception.errorcode.AuthErrorCode.*;

import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.utopia.utopia_be.auth.dto.response.KakaoUserInfoResponse;
import com.utopia.utopia_be.auth.dto.response.LoginResponse;
import com.utopia.utopia_be.auth.exception.AuthException;
import com.utopia.utopia_be.auth.util.JwtTokenProvider;
import com.utopia.utopia_be.auth.util.kakao.KakaoApiClient;
import com.utopia.utopia_be.user.domain.User;
import com.utopia.utopia_be.user.domain.type.LoginType;
import com.utopia.utopia_be.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;

  private final Map<LoginType, Function<String, LoginResponse>> loginStrategyMap =
      Map.of(
          LoginType.KAKAO, this::kakaoLogin,
          LoginType.NAVER, this::naverLogin,
          LoginType.GOOGLE, this::googleLogin);
  private final KakaoApiClient kakaoApiClient;

  public LoginResponse socialLogin(LoginType loginType, String code) {
    Function<String, LoginResponse> strategy = loginStrategyMap.get(loginType);
    if (strategy == null) {
      throw new AuthException(LOGIN_TYPE_NOT_SUPPORTED);
    }
    return strategy.apply(code);
  }

  private LoginResponse kakaoLogin(String code) {
    String accessToken =
        kakaoApiClient.getAccessToken(code); // 1. Authorization Code를 Access Token으로 교환
    User user = isSignedUp(accessToken); // 2. Access Token을 이용해 사용자 정보를 가져오고 없으면 회원가입
    String token = jwtTokenProvider.createToken(user.getId().toString()); // 3. JWT 토큰 생성

    return LoginResponse.from(user.getNickname(), user.getRole(), token);
  }

  private LoginResponse naverLogin(String code) {
    return null;
  }

  private LoginResponse googleLogin(String code) {
    return null;
  }

  private User isSignedUp(String accessToken) {
    KakaoUserInfoResponse userInfo = kakaoApiClient.getUserInfo(accessToken);
    return findOrCreateUser(userInfo.kakaoAccount().email(), LoginType.KAKAO);
  }

  @Transactional
  public User findOrCreateUser(String email, LoginType loginType) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseGet(
                () ->
                    userRepository.save(User.builder().email(email).loginType(loginType).build()));
    return user;
  }
}
