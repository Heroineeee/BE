package com.kkinikong.be.feedback.util.notion;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.feedback.event.payload.FeedbackCreatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotionClient {

  private final WebClient webClient;

  @Value("${external-api.notion.secret}")
  private String secret;

  @Value("${external-api.notion.database-id.feedback}")
  private String feedbackId;

  public void sendFeedback(FeedbackCreatedEvent feedbackCreatedEvent) {

    // 현재 날짜
    String today = LocalDate.now().toString();

    Map<String, Object> body =
        Map.of(
            "parent", Map.of("database_id", feedbackId),
            "properties",
                Map.of(
                    "내용",
                        Map.of(
                            "title",
                            List.of(
                                Map.of("text", Map.of("content", feedbackCreatedEvent.content())))),
                    "별점", Map.of("number", feedbackCreatedEvent.rating()),
                    "유형", Map.of("select", Map.of("name", feedbackCreatedEvent.type())),
                    "피드백 날짜", Map.of("date", Map.of("start", today))));

    webClient
        .post()
        .uri("https://api.notion.com/v1/pages")
        .header("Authorization", "Bearer " + secret)
        .header("Notion-Version", "2022-06-28")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(String.class)
        .doOnSuccess(res -> log.info("노션 피드백 기록 성공"))
        .doOnError(err -> log.error("노션 기록 실패: {}", err.getMessage()))
        .subscribe();
  }
}
