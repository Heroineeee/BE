package com.kkinikong.be.auth.service.strategy;

import com.kkinikong.be.auth.dto.response.LoginResponse;

public interface SocialLoginStrategy {
  LoginResponse login(String code);
}
