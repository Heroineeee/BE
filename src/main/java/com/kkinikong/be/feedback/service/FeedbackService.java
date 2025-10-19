package com.kkinikong.be.feedback.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.feedback.domain.Feedback;
import com.kkinikong.be.feedback.dto.request.FeedbackRequest;
import com.kkinikong.be.feedback.repository.FeedbackRepository;
import com.kkinikong.be.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class FeedbackService {

  private final FeedbackRepository feedbackRepository;
  private final UserRepository userRepository;

  @Transactional
  public void addFeedback(FeedbackRequest request) {
    Feedback feedback =
        Feedback.builder()
            .rating(request.rating())
            .content(request.content())
            .type(request.type())
            .build();

    feedbackRepository.save(feedback);
  }
}
