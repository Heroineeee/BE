package com.kkinikong.be.feedback.service;

import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.feedback.domain.Feedback;
import com.kkinikong.be.feedback.dto.request.FeedbackRequest;
import com.kkinikong.be.feedback.event.payload.FeedbackCreatedEvent;
import com.kkinikong.be.feedback.repository.FeedbackRepository;

@Service
@RequiredArgsConstructor
public class FeedbackService {

  private final FeedbackRepository feedbackRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public void addFeedback(FeedbackRequest request) {
    String type = request.type().stream().map(Enum::name).collect(Collectors.joining(","));

    Feedback feedback =
        Feedback.builder().rating(request.rating()).content(request.content()).type(type).build();

    feedbackRepository.save(feedback);

    eventPublisher.publishEvent(FeedbackCreatedEvent.from(feedback));
  }
}
