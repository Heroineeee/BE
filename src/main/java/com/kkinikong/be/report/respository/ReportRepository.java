package com.kkinikong.be.report.respository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kkinikong.be.report.domain.Report;
import com.kkinikong.be.report.domain.type.ReportType;

public interface ReportRepository extends JpaRepository<Report, Long> {

  boolean existsReportByReportTypeAndTargetIdAndUserId(ReportType type, Long targetId, Long userId);

  boolean existsReportByTargetIdAndUserIdAndCreatedDateAfter(
      Long targetId, Long userId, LocalDateTime localDateTime);
}
