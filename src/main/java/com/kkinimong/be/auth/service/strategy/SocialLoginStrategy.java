package com.kkinimong.be.auth.service.strategy;

import com.kkinimong.be.auth.dto.response.LoginResponse;

public interface SocialLoginStrategy {
  LoginResponse login(String code);
}
