package com.kkinikong.be.report.event.payload;

import com.kkinikong.be.report.domain.Report;
import com.kkinikong.be.report.domain.type.ReportType;

public record ReportCreatedEvent(
    ReportType reportType,
    Long targetId,
    Long userId,
    Long targetUserId,
    String reason,
    String description) {
  public static ReportCreatedEvent from(Report report, Long targetUserId) {
    return new ReportCreatedEvent(
        report.getReportType(),
        report.getTargetId(),
        report.getUser().getId(),
        targetUserId,
        report.getReason(),
        report.getDescription());
  }
}
