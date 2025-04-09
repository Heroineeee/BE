package com.kkinikong.be.user.service;

import static com.kkinikong.be.user.exception.errorcode.UserErrorCode.DUPLICATE_NICKNAME;
import static com.kkinikong.be.user.exception.errorcode.UserErrorCode.USER_NOT_FOUND;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.domain.type.LoginType;
import com.kkinikong.be.user.dto.request.NicknameRequest;
import com.kkinikong.be.user.exception.UserException;
import com.kkinikong.be.user.repository.UserRepository;

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

  public void updateNickname(NicknameRequest request, Long userId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserException(USER_NOT_FOUND));
    if (userRepository.existsByNickname(request.nickname())) {
      throw new UserException(DUPLICATE_NICKNAME);
    }
    user.updateNickname(request.nickname());
    userRepository.save(user);
  }
}
