package com.utopia.utopia_be.auth.service.strategy;

import static com.utopia.utopia_be.auth.exception.errorcode.AuthErrorCode.*;

import org.springframework.stereotype.Service;

import com.utopia.utopia_be.auth.dto.response.LoginResponse;
import com.utopia.utopia_be.auth.exception.AuthException;

@Service("GOOGLE")
public class GoogleLoginStrategy implements SocialLoginStrategy {

  @Override
  public LoginResponse login(String code) {
    throw new AuthException(LOGIN_TYPE_NOT_SUPPORTED);
  }
}
