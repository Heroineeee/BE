package com.kkinikong.be.report.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.report.event.payload.ReportCreatedEvent;
import com.kkinikong.be.report.util.discord.DiscordClient;
import com.kkinikong.be.report.util.notion.NotionClient;

@Component
@RequiredArgsConstructor
public class ReportEventListener {

  private final NotionClient notionClient;
  private final DiscordClient discordClient;

  @Async("externalApiExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleReportCreatedEvent(ReportCreatedEvent reportCreatedEvent) {
    discordClient.sendReportAlert(reportCreatedEvent);
    notionClient.sendReport(reportCreatedEvent);
  }
}
