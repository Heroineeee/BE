package com.kkinimong.be.user.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.kkinimong.be.user.domain.User;
import com.kkinimong.be.user.domain.type.LoginType;
import com.kkinimong.be.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  public User findOrCreateUser(String email, LoginType loginType) {
    return userRepository
        .findByEmailAndLoginType(email, loginType)
        .orElseGet(
            () ->
                userRepository.save(
                    User.socialLoginBuilder()
                        .email(email)
                        .loginType(loginType)
                        .buildSocialLogin()));
  }
}
