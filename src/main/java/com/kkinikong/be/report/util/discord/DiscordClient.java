package com.kkinikong.be.report.util.discord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.report.event.payload.ReportCreatedEvent;

@Slf4j
@Component("reportDiscordClient")
@RequiredArgsConstructor
public class DiscordClient {

  private final WebClient webClient;

  @Value("${external-api.discord.report-url}")
  private String reportUrl;

  public void sendReportAlert(ReportCreatedEvent reportCreatedEvent) {
    // 디스코드 메시지 바디 구성
    String content =
        String.format(
            "**신고 유형**: %s\n"
                + "**신고 사유**: %s\n"
                + "**타겟 ID**: %d\n"
                + "**신고자**: %s \n"
                + "**상세 내용**: %s",
            reportCreatedEvent.reportType().getLabel(),
            reportCreatedEvent.reason(),
            reportCreatedEvent.targetId(),
            reportCreatedEvent.userNickname(),
            reportCreatedEvent.description());

    Map<String, Object> body =
        Map.of(
            "embeds",
            List.of(
                Map.of(
                    "title",
                    "📌 신고 리포트 알림",
                    "description",
                    content,
                    "color",
                    16711680, // 빨간색
                    "timestamp",
                    LocalDateTime.now().toString())));

    // WebClient 호출
    webClient
        .post()
        .uri(reportUrl)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(Void.class)
        .doOnSuccess(res -> log.info("디스코드 신고 알림 전송 성공"))
        .doOnError(err -> log.error("디스코드 알림 실패: {}", err.getMessage()))
        .subscribe();
  }
}
