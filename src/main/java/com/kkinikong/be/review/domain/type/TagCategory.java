package com.kkinikong.be.review.domain.type;

import lombok.Getter;

@Getter
public enum TagCategory {
  MENU("메뉴"),
  SPACE("공간"),
  PAYMENT("결제"),
  ETC("기타");

  private final String label;

  TagCategory(String label) {
    this.label = label;
  }
}
