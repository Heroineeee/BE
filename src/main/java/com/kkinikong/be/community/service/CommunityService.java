package com.kkinikong.be.community.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.dto.request.CommunityPostRequest;
import com.kkinikong.be.community.dto.response.CommunityPostResponse;
import com.kkinikong.be.community.repository.CommunityRepository;
import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.exception.UserException;
import com.kkinikong.be.user.exception.errorcode.UserErrorCode;
import com.kkinikong.be.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommunityService {

  private final CommunityRepository communityRepository;
  private final UserRepository userRepository;

  @Transactional
  public CommunityPostResponse postCommunityPost(CommunityPostRequest request, Long userId) {
    CommunityPost communityPost =
        communityRepository.save(
            CommunityPost.builder()
                .title(request.title())
                .content(request.content())
                .category(request.category())
                .user(getUserOrThrow(userId))
                .build());

    return new CommunityPostResponse(communityPost.getId());
  }

  private User getUserOrThrow(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
  }
}
