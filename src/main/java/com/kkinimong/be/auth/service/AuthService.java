package com.kkinimong.be.auth.service;

import static com.kkinimong.be.auth.exception.errorcode.AuthErrorCode.*;
import static com.kkinimong.be.user.exception.errorcode.UserErrorCode.*;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinimong.be.auth.dto.request.BasicLoginRequest;
import com.kkinimong.be.auth.dto.response.LoginResponse;
import com.kkinimong.be.auth.exception.AuthException;
import com.kkinimong.be.auth.service.strategy.SocialLoginStrategy;
import com.kkinimong.be.auth.util.JwtTokenProvider;
import com.kkinimong.be.user.domain.User;
import com.kkinimong.be.user.domain.type.LoginType;
import com.kkinimong.be.user.exception.UserException;
import com.kkinimong.be.user.repository.UserRepository;

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
