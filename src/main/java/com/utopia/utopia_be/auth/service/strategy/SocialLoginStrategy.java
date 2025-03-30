package com.utopia.utopia_be.auth.service.strategy;

import org.springframework.stereotype.Service;

import com.utopia.utopia_be.auth.dto.response.LoginResponse;

@Service
public interface SocialLoginStrategy {
  LoginResponse login(String code);
}
