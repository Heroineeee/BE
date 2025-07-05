package com.kkinikong.be.user.utils;

import com.kkinikong.be.user.domain.User;

public class UserNicknameUtil {
  public static String displayNickname(User user) {
    return user.isDeleted() ? "탈퇴한 회원입니다" : user.getNickname();
  }

  public static String checkReportNickname(User user) {
    String nickname = displayNickname(user);
    if (!nickname.equals("탈퇴한 회원입니다")) {
      nickname = nickname.charAt(0) + "***";
    }
    return nickname;
  }
}
