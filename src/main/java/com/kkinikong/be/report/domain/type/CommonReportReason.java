package com.kkinikong.be.report.domain.type;

import lombok.Getter;

@Getter
public enum CommonReportReason {
  ABUSIVE_LANGUAGE("욕설/비방"),
  FAKE_INFO("허위 정보"),
  SPAM("스팸/도배"),
  PRIVACY("개인정보 노출"),
  ETC("기타");

  private final String label;

  CommonReportReason(String label) {
    this.label = label;
  }
}
