package com.kkinikong.be.auth.service;

import static com.kkinikong.be.auth.exception.errorcode.AuthErrorCode.*;
import static com.kkinikong.be.user.exception.errorcode.UserErrorCode.*;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.auth.dto.request.BasicLoginRequest;
import com.kkinikong.be.auth.dto.response.LoginResponse;
import com.kkinikong.be.auth.exception.AuthException;
import com.kkinikong.be.auth.service.strategy.SocialLoginStrategy;
import com.kkinikong.be.auth.util.JwtTokenProvider;
import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.domain.type.LoginType;
import com.kkinikong.be.user.exception.UserException;
import com.kkinikong.be.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;

  private final Map<String, SocialLoginStrategy> loginStrategyMap;

  public LoginResponse socialLogin(LoginType loginType, String code) {
    SocialLoginStrategy loginStrategy = loginStrategyMap.get(loginType.name());

    if (loginStrategy == null) {
      throw new AuthException(LOGIN_TYPE_NOT_SUPPORTED);
    }

    return loginStrategy.login(code);
  }

  public void signup(BasicLoginRequest request) {
    String email = request.email();

    if (userRepository.existsByEmail(email)) {
      throw new UserException(USER_ALREADY_EXISTS);
    }

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
