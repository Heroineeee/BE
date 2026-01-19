package com.kkinikong.be.feedback.util.discord;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.feedback.event.payload.FeedbackCreatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordClient {

  private final WebClient webClient;

  @Value("${external-api.discord.feedback-url}")
  private String feedbackUrl;

  public void sendFeedbackAlert(FeedbackCreatedEvent feedbackCreatedEvent) {
    // 디스코드 규격에 맞는 JSON 바디 생성
    Map<String, Object> body =
        Map.of(
            "embeds",
            List.of(
                Map.of(
                    "title",
                    "\uD83D\uDCCC 유저의 피드백 도착",
                    "color",
                    5814783,
                    "fields",
                    List.of(
                        Map.of(
                            "name",
                            "⭐ 별점",
                            "value",
                            feedbackCreatedEvent.rating() + "점",
                            "inline",
                            false),
                        Map.of(
                            "name",
                            "🏷️ 유형",
                            "value",
                            feedbackCreatedEvent.type(),
                            "inline",
                            false),
                        Map.of(
                            "name",
                            "📝 내용",
                            "value",
                            feedbackCreatedEvent.content(),
                            "inline",
                            false)))));

    // WebClient를 이용한 비동기 전송
    webClient
        .post()
        .uri(feedbackUrl)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(String.class)
        .doOnSuccess(response -> log.info("디스코드 알림 전송 성공"))
        .doOnError(error -> log.error("디스코드 알림 전송 실패: {}", error.getMessage()))
        .subscribe();
  }
}
