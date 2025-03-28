package com.utopia.utopia_be.auth.service;

import static com.utopia.utopia_be.auth.exception.errorcode.AuthErrorCode.*;

import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.utopia.utopia_be.auth.exception.AuthException;
import com.utopia.utopia_be.user.domain.type.LoginType;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

  private final Map<LoginType, Function<String, String>> loginStrategyMap =
      Map.of(
          LoginType.KAKAO, this::kakaoLogin,
          LoginType.NAVER, this::naverLogin,
          LoginType.GOOGLE, this::googleLogin);

  public String socialLogin(LoginType loginType, String code) {
    Function<String, String> strategy = loginStrategyMap.get(loginType);
    if (strategy == null) {
      throw new AuthException(LOGIN_TYPE_NOT_SUPPORTED);
    }
    return strategy.apply(code);
  }

  @Transactional
  public String kakaoLogin(String code) {
    return "kakaoLogin";
  }

  @Transactional
  public String naverLogin(String code) {
    return "naverLogin";
  }

  @Transactional
  public String googleLogin(String code) {
    return "googleLogin";
  }
}
