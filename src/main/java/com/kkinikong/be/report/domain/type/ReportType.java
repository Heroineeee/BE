package com.kkinikong.be.report.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportType {
  STORE("가맹점"),
  REVIEW("리뷰"),
  COMMUNITY_POST("커뮤니티 게시글"),
  COMMUNITY_COMMENT("커뮤니티 댓글");

  private final String label;
}
