package com.utopia.utopia_be.auth.service.strategy;

import com.utopia.utopia_be.auth.dto.response.LoginResponse;

public interface SocialLoginStrategy {
  LoginResponse login(String code);
}
