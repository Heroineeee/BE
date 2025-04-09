package com.kkinikong.be.auth.service.strategy;

import static com.kkinikong.be.auth.exception.errorcode.AuthErrorCode.*;

import org.springframework.stereotype.Service;

import com.kkinikong.be.auth.dto.response.LoginResponse;
import com.kkinikong.be.auth.exception.AuthException;

@Service("NAVER")
public class NaverLoginStrategy implements SocialLoginStrategy {

  @Override
  public LoginResponse login(String code) {
    throw new AuthException(LOGIN_TYPE_NOT_SUPPORTED);
  }
}
