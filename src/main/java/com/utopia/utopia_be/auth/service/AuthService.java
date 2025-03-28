package com.utopia.utopia_be.auth.service;

import static com.utopia.utopia_be.auth.exception.errorcode.AuthErrorCode.*;
import static com.utopia.utopia_be.user.exception.errorcode.UserErrorCode.*;

import java.util.Map;
import java.util.function.Function;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.utopia.utopia_be.auth.dto.request.BasicLoginRequest;
import com.utopia.utopia_be.auth.dto.response.KakaoUserInfoResponse;
import com.utopia.utopia_be.auth.dto.response.LoginResponse;
import com.utopia.utopia_be.auth.exception.AuthException;
import com.utopia.utopia_be.auth.util.JwtTokenProvider;
import com.utopia.utopia_be.auth.util.kakao.KakaoApiClient;
import com.utopia.utopia_be.user.domain.User;
import com.utopia.utopia_be.user.domain.type.LoginType;
import com.utopia.utopia_be.user.exception.UserException;
import com.utopia.utopia_be.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final KakaoApiClient kakaoApiClient;

  private final PasswordEncoder passwordEncoder;

  private final Map<LoginType, Function<String, LoginResponse>> loginStrategyMap =
      Map.of(
          LoginType.KAKAO, this::kakaoLogin,
          LoginType.NAVER, this::naverLogin,
          LoginType.GOOGLE, this::googleLogin);

  public LoginResponse socialLogin(LoginType loginType, String code) {
    Function<String, LoginResponse> strategy = loginStrategyMap.get(loginType);
    if (strategy == null) {
      throw new AuthException(LOGIN_TYPE_NOT_SUPPORTED);
    }
    return strategy.apply(code);
  }

  private LoginResponse kakaoLogin(String code) {
    // Authorization Code를 Access Token으로 교환
    String accessToken = kakaoApiClient.getAccessToken(code);

    // Access Token을 이용해 사용자 정보를 가져오고 없으면 회원가입
    KakaoUserInfoResponse userInfo = kakaoApiClient.getUserInfo(accessToken);
    User user = findOrCreateUser(userInfo.kakao_account().email(), LoginType.KAKAO);

    // JWT 토큰 생성
    String token = jwtTokenProvider.createToken(user.getId().toString());

    return LoginResponse.from(user.getNickname(), user.getRole(), token);
  }

  private LoginResponse naverLogin(String code) {
    return null;
  }

  private LoginResponse googleLogin(String code) {
    return null;
  }

  public User findOrCreateUser(String email, LoginType loginType) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseGet(
                () ->
                    userRepository.save(
                        User.socialLoginBuilder()
                            .email(email)
                            .loginType(loginType)
                            .buildSocialLogin()));
    return user;
  }

  public void signup(BasicLoginRequest request) {
    String email = request.email();

    userRepository
        .existsByEmail(email)
        .ifPresent(
            (exists) -> {
              if (exists) {
                throw new UserException(USER_ALREADY_EXISTS);
              }
            });

    userRepository.save(
        User.basicLoginBuilder()
            .email(email)
            .password(passwordEncoder.encode(request.password()))
            .buildBasicLogin());
  }

  public LoginResponse login(BasicLoginRequest request) {
    User user =
        userRepository
            .findByEmail(request.email())
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      throw new AuthException(INVALID_CREDENTIALS);
    }

    String token = jwtTokenProvider.createToken(user.getId().toString());
    return LoginResponse.from(user.getNickname(), user.getRole(), token);
  }
}
