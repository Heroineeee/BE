package com.kkinikong.be.report.util.notion;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.report.event.payload.ReportCreatedEvent;

@Slf4j
@Component("reportNotionClient")
@RequiredArgsConstructor
public class NotionClient {

  private final WebClient webClient;

  @Value("${external-api.notion.secret}")
  private String secret;

  @Value("${external-api.notion.database-id.report}")
  private String reportDbId;

  public void sendReport(ReportCreatedEvent event) {
    Map<String, Object> props = new java.util.HashMap<>();

    props.put(
        "신고자 닉네임",
        Map.of("title", List.of(Map.of("text", Map.of("content", event.userNickname())))));

    props.put("신고자 ID", Map.of("number", event.userId()));
    props.put("신고 유형", Map.of("select", Map.of("name", event.reportType().name())));
    props.put("대상 ID", Map.of("number", event.targetId()));
    props.put("신고 사유", Map.of("select", Map.of("name", event.reason())));
    props.put(
        "기타 사유",
        Map.of(
            "rich_text",
            List.of(
                Map.of(
                    "text",
                    Map.of("content", event.description() != null ? event.description() : "")))));

    props.put("신고 일시", Map.of("date", Map.of("start", java.time.LocalDate.now().toString())));

    Map<String, Object> body =
        Map.of("parent", Map.of("database_id", reportDbId), "properties", props);

    // WebClient 전송 로직
    webClient
        .post()
        .uri("https://api.notion.com/v1/pages")
        .header("Authorization", "Bearer " + secret)
        .header("Notion-Version", "2022-06-28")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(String.class)
        .doOnSuccess(res -> log.info("노션 신고 기록 성공"))
        .doOnError(
            err -> {
              if (err instanceof WebClientResponseException ex) {
                log.error("노션 상세 에러 메시지: {}", ex.getResponseBodyAsString());
              }
              log.error("노션 기록 실패: {}", err.getMessage());
            })
        .subscribe();
  }
}
