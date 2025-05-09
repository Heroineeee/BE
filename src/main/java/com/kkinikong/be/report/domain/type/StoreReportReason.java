package com.kkinikong.be.report.domain.type;

import lombok.Getter;

@Getter
public enum StoreReportReason {
  CATEGORY("음식 카테고리"),
  LOCATION("위치"),
  BUSINESS_HOURS("영업시간"),
  CLOSED("폐업한 가게"),
  ETC("기타");

  private final String label;

  StoreReportReason(String label) {
    this.label = label;
  }
}
