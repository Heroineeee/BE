package com.kkinikong.be.user.utils;

import com.kkinikong.be.user.domain.User;

public class UserNicknameUtil {
  public static String displayNickname(User user) {
    return user.isDeleted() ? "탈퇴한 회원입니다." : user.getNickname();
  }
}
