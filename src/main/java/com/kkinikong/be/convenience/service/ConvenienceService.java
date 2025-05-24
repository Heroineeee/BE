package com.kkinikong.be.convenience.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.convenience.domain.ConveniencePost;
import com.kkinikong.be.convenience.dto.request.ConvenienceRequest;
import com.kkinikong.be.convenience.dto.response.ConveniencePostResponse;
import com.kkinikong.be.convenience.repository.ConvenienceRepository;
import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.exception.UserException;
import com.kkinikong.be.user.exception.errorcode.UserErrorCode;
import com.kkinikong.be.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConvenienceService {

  private final ConvenienceRepository convenienceRepository;
  private final UserRepository userRepository;

  @Transactional
  public ConveniencePostResponse postConvenience(ConvenienceRequest request, Long userId) {
    User user = getUserOrThrow(userId);
    ConveniencePost conveniencePost =
        convenienceRepository.save(
            ConveniencePost.builder()
                .name(request.name())
                .brand(request.brand())
                .category(request.category())
                .description(request.description())
                .isAvailable(request.isAvailable())
                .user(user)
                .build());
    return new ConveniencePostResponse(conveniencePost.getId());
  }

  private User getUserOrThrow(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
  }
}
