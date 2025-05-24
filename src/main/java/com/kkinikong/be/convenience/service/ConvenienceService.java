package com.kkinikong.be.convenience.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.convenience.domain.ConvenienceHelpful;
import com.kkinikong.be.convenience.domain.ConveniencePost;
import com.kkinikong.be.convenience.dto.request.ConvenienceRequest;
import com.kkinikong.be.convenience.dto.response.ConveniencePostResponse;
import com.kkinikong.be.convenience.dto.response.ConvenienceRecommendationResponse;
import com.kkinikong.be.convenience.exception.ConvenienceException;
import com.kkinikong.be.convenience.exception.errorcode.ConvenienceErrorCode;
import com.kkinikong.be.convenience.repository.ConvenienceHelpfulRepository;
import com.kkinikong.be.convenience.repository.ConvenienceRepository;
import com.kkinikong.be.convenience.util.OpenAIApiClient;
import com.kkinikong.be.convenience.util.dto.OpenAIRequest;
import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.exception.UserException;
import com.kkinikong.be.user.exception.errorcode.UserErrorCode;
import com.kkinikong.be.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ConvenienceService {

  private final ConvenienceRepository convenienceRepository;
  private final UserRepository userRepository;
  private final ConvenienceHelpfulRepository helpfulRepository;
  private final OpenAIApiClient openAIApiClient;

  public ConvenienceRecommendationResponse getProductNameRecommendation(String productName) {
    OpenAIRequest openAIRequest = new OpenAIRequest(productName);
    return openAIApiClient.getProductNameRecommendation(openAIRequest);
  }

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

  @Transactional
  public void deleteConveniencePost(Long postId, Long userId) {
    ConveniencePost conveniencePost = getConveniencePostOrThrow(postId);
    validateConveniencePostOwner(conveniencePost, userId);
    convenienceRepository.delete(conveniencePost);
  }

  @Transactional
  public void addConveniencePostInfo(Long postId, Boolean isCorrect, Long userId) {
    ConveniencePost conveniencePost = getConveniencePostOrThrow(postId);
    User user = getUserOrThrow(userId);

    Optional<ConvenienceHelpful> optionalHelpful =
        helpfulRepository.findByConveniencePostAndUser(conveniencePost, user);

    if (optionalHelpful.isPresent()) {
      ConvenienceHelpful existing = optionalHelpful.get();
      if (existing.getIsCorrect().equals(isCorrect)) return;

      conveniencePost.decreaseCount(existing.getIsCorrect());
      existing.updateIsCorrect(isCorrect);
    } else {
      ConvenienceHelpful newHelpful =
          ConvenienceHelpful.builder()
              .user(user)
              .conveniencePost(conveniencePost)
              .isCorrect(isCorrect)
              .build();
      helpfulRepository.save(newHelpful);
    }
    conveniencePost.increaseCount(isCorrect);
  }

  private User getUserOrThrow(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
  }

  private ConveniencePost getConveniencePostOrThrow(Long conveniencePostId) {
    return convenienceRepository
        .findById(conveniencePostId)
        .orElseThrow(
            () -> new ConvenienceException(ConvenienceErrorCode.CONVENIENCE_POST_NOT_FOUND));
  }

  private void validateConveniencePostOwner(ConveniencePost conveniencePost, Long userId) {
    if (!conveniencePost.getUser().getId().equals(userId)) {
      throw new ConvenienceException(ConvenienceErrorCode.CONVENIENCE_POST_NOT_AUTHORIZED);
    }
  }
}
