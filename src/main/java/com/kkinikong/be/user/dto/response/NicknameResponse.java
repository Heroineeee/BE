package com.kkinikong.be.user.dto.response;

import com.kkinikong.be.user.domain.User;

public record NicknameResponse(String email, String nickname, boolean isNicknameModified) {

  public static NicknameResponse from(User user) {
    return new NicknameResponse(user.getEmail(), user.getNickname(), user.isNicknameModified());
  }
}
