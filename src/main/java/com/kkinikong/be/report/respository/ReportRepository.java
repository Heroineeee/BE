package com.kkinikong.be.report.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kkinikong.be.report.domain.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {

  boolean existsReportByTargetIdAndUserId(Long targetId, Long userId);
}
