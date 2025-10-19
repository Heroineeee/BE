package com.kkinikong.be.feedback.domain.type;

import lombok.Getter;

@Getter
public enum FeedbackType {
  SEARCH_FOOD("음식결과없음"),
  SEARCH_RESTAURANT("식당결과없음"),
  SEARCH_COMMUNITY("커뮤니티결과없음");

  private final String label;

  FeedbackType(String label) {
    this.label = label;
  }
}
