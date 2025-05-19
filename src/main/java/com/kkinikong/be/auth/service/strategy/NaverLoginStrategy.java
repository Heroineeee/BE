package com.kkinikong.be.auth.service.strategy;

import static com.kkinikong.be.auth.exception.errorcode.AuthErrorCode.*;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.auth.dto.response.LoginResponse;
import com.kkinikong.be.auth.dto.response.naver.NaverUserInfoResponse;
import com.kkinikong.be.auth.util.JwtTokenProvider;
import com.kkinikong.be.auth.util.naver.NaverApiClient;
import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.domain.type.LoginType;
import com.kkinikong.be.user.service.UserService;

@Service("NAVER")
@RequiredArgsConstructor
public class NaverLoginStrategy implements SocialLoginStrategy {

  private final NaverApiClient naverApiClient;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserService userService;

  @Override
  public LoginResponse login(String code) {
    String accessToken = naverApiClient.getAccessToken(code);
    NaverUserInfoResponse userInfo = naverApiClient.getUserInfo(accessToken);
    User user = userService.findOrCreateUser(userInfo.email(), LoginType.NAVER);
    String token = jwtTokenProvider.createToken(user.getId().toString());
    return LoginResponse.from(user.getNickname(), user.getRole(), token);
  }
}
