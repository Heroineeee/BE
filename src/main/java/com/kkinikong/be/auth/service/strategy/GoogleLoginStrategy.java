package com.kkinikong.be.auth.service.strategy;

import static com.kkinikong.be.auth.exception.errorcode.AuthErrorCode.*;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.auth.dto.response.LoginResponse;
import com.kkinikong.be.auth.dto.response.google.GoogleUserInfoResponse;
import com.kkinikong.be.auth.util.JwtTokenProvider;
import com.kkinikong.be.auth.util.google.GoogleApiClient;
import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.domain.type.LoginType;
import com.kkinikong.be.user.service.UserService;

@Service("GOOGLE")
@RequiredArgsConstructor
public class GoogleLoginStrategy implements SocialLoginStrategy {

  private final GoogleApiClient googleApiClient;
  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public LoginResponse login(String code) {
    String accessToken = googleApiClient.getAccessToken(code);
    GoogleUserInfoResponse userInfo = googleApiClient.getUserInfo(accessToken);
    User user = userService.findOrCreateUser(userInfo.email(), LoginType.GOOGLE);
    String token = jwtTokenProvider.createToken(user.getId().toString());
    return LoginResponse.from(user.getNickname(), user.getRole(), token);
  }
}
