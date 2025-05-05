package com.kkinikong.be.report.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.global.entity.BaseEntity;
import com.kkinikong.be.report.domain.type.ReportType;
import com.kkinikong.be.user.domain.User;

@Table(
    name = "reports",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "report_type", "target_id"}))
@Entity
@Getter
@NoArgsConstructor
public class Report extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "report_type", nullable = false)
  private ReportType reportType;

  @Column(name = "target_id", nullable = false)
  private Long targetId;

  @Column(name = "reason", nullable = false)
  private String reason;

  @Column(name = "description", length = 500)
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
