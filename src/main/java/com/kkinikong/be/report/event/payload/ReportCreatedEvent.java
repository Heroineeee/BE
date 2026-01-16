package com.kkinikong.be.report.event.payload;

import com.kkinikong.be.report.domain.Report;
import com.kkinikong.be.report.domain.type.ReportType;

public record ReportCreatedEvent(
    ReportType reportType,
    Long targetId,
    Long userId,
    String userNickname,
    String reason,
    String description) {
  public static ReportCreatedEvent from(Report report) {
    return new ReportCreatedEvent(
        report.getReportType(),
        report.getTargetId(),
        report.getUser().getId(),
        report.getUser().getNickname(),
        report.getReason(),
        report.getDescription());
  }
}
