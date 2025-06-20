package com.kkinikong.be.feedback.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.feedback.domain.Feedback;
import com.kkinikong.be.feedback.dto.request.FeedbackRequest;
import com.kkinikong.be.feedback.repository.FeedbackRepository;
import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.exception.UserException;
import com.kkinikong.be.user.exception.errorcode.UserErrorCode;
import com.kkinikong.be.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class FeedbackService {

  private final FeedbackRepository feedbackRepository;
  private final UserRepository userRepository;

  @Transactional
  public void addFeedback(FeedbackRequest request, Long userId) {
    User user = getUserOrThrow(userId);
    Feedback feedback =
        Feedback.builder().user(user).rating(request.rating()).content(request.content()).build();
    feedbackRepository.save(feedback);
  }

  private User getUserOrThrow(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
  }
}
