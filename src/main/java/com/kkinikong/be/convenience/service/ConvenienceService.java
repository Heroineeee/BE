package com.kkinikong.be.convenience.service;

import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.convenience.domain.ConvenienceHelpful;
import com.kkinikong.be.convenience.domain.ConveniencePost;
import com.kkinikong.be.convenience.domain.type.Brand;
import com.kkinikong.be.convenience.domain.type.Category;
import com.kkinikong.be.convenience.dto.request.ConvenienceRequest;
import com.kkinikong.be.convenience.dto.response.ConveniencePostDetailResponse;
import com.kkinikong.be.convenience.dto.response.ConveniencePostInfoResponse;
import com.kkinikong.be.convenience.dto.response.ConveniencePostListResponse;
import com.kkinikong.be.convenience.dto.response.ConveniencePostResponse;
import com.kkinikong.be.convenience.dto.response.ConvenienceRecommendationResponse;
import com.kkinikong.be.convenience.exception.ConvenienceException;
import com.kkinikong.be.convenience.exception.errorcode.ConvenienceErrorCode;
import com.kkinikong.be.convenience.repository.ConvenienceHelpfulRepository;
import com.kkinikong.be.convenience.repository.ConvenienceRepository;
import com.kkinikong.be.convenience.util.OpenAIApiClient;
import com.kkinikong.be.convenience.util.dto.OpenAIRequest;
import com.kkinikong.be.global.response.PageResponse;
import com.kkinikong.be.notification.domain.type.NotificationType;
import com.kkinikong.be.notification.event.NotificationEvent;
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
  private final ApplicationEventPublisher eventPublisher;

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
  public ConveniencePostInfoResponse addConveniencePostInfo(
      Long postId, Boolean isCorrect, Long userId) {
    ConveniencePost conveniencePost = getConveniencePostOrThrow(postId);
    User user = getUserOrThrow(userId);

    // 본인이 작성한 게시글에 대해서는 선택 불가능
    if (conveniencePost.getUser().getId().equals(user.getId())) {
      throw new ConvenienceException(ConvenienceErrorCode.NOT_ALLOWED_TO_SELECT_OWN_POST);
    }

    // 이전에 해당 게시글에 대해 사용자의 선택이 있는지 확인
    Optional<ConvenienceHelpful> optionalHelpful =
        helpfulRepository.findByConveniencePostAndUser(conveniencePost, user);

    // 기존 선택과 동일한 값이면 취소 상태로 변환
    if (optionalHelpful.isPresent()) {
      ConvenienceHelpful existing = optionalHelpful.get();
      if (existing.getIsCorrect().equals(isCorrect)) {
        helpfulRepository.delete(existing);
        conveniencePost.decreaseCount(isCorrect);
        return new ConveniencePostInfoResponse(
            conveniencePost.getCorrectCount(), conveniencePost.getIncorrectCount(), null);
      }

      // 다른 거를 선택했으면 상태 전환
      conveniencePost.decreaseCount(existing.getIsCorrect());
      existing.updateIsCorrect(isCorrect);
    } else {
      // 첫 선택일 경우
      ConvenienceHelpful newHelpful =
          ConvenienceHelpful.builder()
              .user(user)
              .conveniencePost(conveniencePost)
              .isCorrect(isCorrect)
              .build();
      helpfulRepository.save(newHelpful);

      // 알림 이벤트 발행 : '올바른 정보예요' 인 경우
      if (isCorrect) {
        eventPublisher.publishEvent(
            NotificationEvent.builder()
                .receiver(conveniencePost.getUser())
                .type(NotificationType.CONVENIENCE_CORRECT_INFO)
                .senderNickname(user.getNickname())
                .target(conveniencePost.getName())
                .targetId(conveniencePost.getId())
                .redirectUrl("/convenience/post/" + conveniencePost.getId())
                .build());
      }
    }

    conveniencePost.increaseCount(isCorrect);

    return new ConveniencePostInfoResponse(
        conveniencePost.getCorrectCount(), conveniencePost.getIncorrectCount(), isCorrect);
  }

  public PageResponse<ConveniencePostListResponse> getConveniencePostList(
      String keyword,
      Category category,
      Brand brand,
      boolean isAvailableCheck,
      int page,
      int size) {
    Pageable pageable = PageRequest.of(page, size);

    Page<ConveniencePost> allByBrand =
        convenienceRepository.findByCondition(keyword, brand, category, isAvailableCheck, pageable);

    return PageResponse.from(allByBrand, ConveniencePostListResponse::from);
  }

  public ConveniencePostDetailResponse getConveniencePostDetail(Long postId, Long userId) {
    ConveniencePost conveniencePost = getConveniencePostOrThrow(postId);
    User user = (userId != null) ? getUserOrThrow(userId) : null;
    return ConveniencePostDetailResponse.from(user, conveniencePost);
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
