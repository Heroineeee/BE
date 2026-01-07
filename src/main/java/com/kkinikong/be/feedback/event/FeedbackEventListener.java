package com.kkinikong.be.feedback.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.feedback.event.payload.FeedbackCreatedEvent;
import com.kkinikong.be.feedback.util.discord.DiscordClient;

@Component
@RequiredArgsConstructor
public class FeedbackEventListener {

  private final DiscordClient discordClient;

  @Async("externalApiExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleFeedbackCreatedEvent(FeedbackCreatedEvent feedbackCreatedEvent) {
    discordClient.sendFeedbackAlert(feedbackCreatedEvent);
  }
}
