package com.kkinikong.be.feedback.event.payload;

import com.kkinikong.be.feedback.domain.Feedback;

public record FeedbackCreatedEvent(int rating, String content, String type) {
  public static FeedbackCreatedEvent from(Feedback feedback) {
    return new FeedbackCreatedEvent(
        feedback.getRating(), feedback.getContent(), feedback.getType());
  }
}
