package com.kkinikong.be.store.domain.type;

public enum TagCategory {
  MENU("메뉴"),
  SPACE("공간"),
  PAYMENT("결제"),
  ETC("기타");

  private final String label;

  TagCategory(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
