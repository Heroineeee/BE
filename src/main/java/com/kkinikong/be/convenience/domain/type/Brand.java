package com.kkinikong.be.convenience.domain.type;

import lombok.Getter;

@Getter
public enum Brand {
  GS25("지에스25"),
  CU("씨유"),
  SEVENELEVEN("세븐일레븐"),
  EMART24("이마트24"),
  MINISTOP("미니스톱");

  private final String label;

  Brand(String label) {
    this.label = label;
  }
}
