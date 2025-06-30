package com.kkinikong.be.user.service;

import static com.kkinikong.be.user.exception.errorcode.UserErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.StoreScrap;
import com.kkinikong.be.store.repository.storescrap.StoreScrapRepository;
import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.domain.type.LoginType;
import com.kkinikong.be.user.dto.request.NicknameRequest;
import com.kkinikong.be.user.dto.response.NicknameResponse;
import com.kkinikong.be.user.dto.response.UserPlaceResponse;
import com.kkinikong.be.user.exception.UserException;
import com.kkinikong.be.user.exception.errorcode.UserErrorCode;
import com.kkinikong.be.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final StoreScrapRepository storeScrapRepository;

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

  @Transactional
  public NicknameResponse addNickname(NicknameRequest request, Long userId) {
    User user = getUserOrThrow(userId);
    if (userRepository.existsByNickname(request.nickname())) {
      throw new UserException(DUPLICATE_NICKNAME);
    }
    user.updateNickname(request.nickname());
    return NicknameResponse.from(userRepository.save(user));
  }

  public boolean checkNickname(String nickname) {
    return userRepository.existsByNickname(nickname);
  }

  @Transactional
  public void updateNickname(NicknameRequest request, Long userId) {
    User user = getUserOrThrow(userId);
    if (user.isNicknameModified()) {
      throw new UserException(NICKNAME_ALREADY_MODIFIED);
    }
    user.updateNickname(request.nickname());
    user.setNicknameModified(true);
  }

  @Transactional
  public void setUserPlace(Long userId, Double latitude, Double longitude) {
    User user = getUserOrThrow(userId);
    user.updatePlace(latitude, longitude);
  }

  public UserPlaceResponse getUserPlace(Long userId) {
    User user = getUserOrThrow(userId);
    return new UserPlaceResponse(user.getPlaceLatitude(), user.getPlaceLongitude());
  }

  @Transactional
  public void deleteUser(Long userId) {
    User user = getUserOrThrow(userId);
    if (user.isDeleted()) {
      throw new UserException(USER_ALREADY_DELETED);
    }
    List<StoreScrap> scrapList = storeScrapRepository.findAllByUser(user);
    for (StoreScrap scrap : scrapList) {
      Store store = scrap.getStore();
      store.decreaseScrapCount();
    }
    storeScrapRepository.deleteAllByUser(user);
    user.withdraw();
  }

  public NicknameResponse getNickname(Long userId) {
    User user = getUserOrThrow(userId);
    return NicknameResponse.from(user);
  }

  private User getUserOrThrow(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
  }
}
