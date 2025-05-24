package com.kkinikong.be.convenience.domain.type;

import lombok.Getter;

@Getter
public enum Category {
  MEAL("식사"),
  SNACK("간식"),
  DRINK("음료"),
  FRUIT("과일"),
  ETC("기타");
  private final String label;

  Category(String label) {
    this.label = label;
  }
}
