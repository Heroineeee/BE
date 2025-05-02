package com.kkinikong.be.store.domain.type;

import lombok.Getter;

import com.kkinikong.be.store.exception.StoreException;
import com.kkinikong.be.store.exception.errorcode.StoreErrorCode;

@Getter
public enum Category {
  KOREAN("한식"),
  WESTERN("양식"),
  JAPANESE("일식"),
  CHINESE("중식"),
  CHICKEN("치킨"),
  BUNSIK("분식"),
  SHABU("샤브샤브"),
  ASIAN("아시아음식"),
  LUNCHBOX("도시락"),
  DESSERT("간식"),
  ETC("기타");

  private final String label;

  Category(String label) {
    this.label = label;
  }

  // csv 파일의 한글 라벨을 기반으로 Category enum을 반환
  public static Category fromLabel(String label) {
    for (Category category : Category.values()) {
      if (category.label.equals(label)) {
        return category;
      }
    }
    throw new StoreException(StoreErrorCode.INVALID_CATEGORY_LABEL);
  }
}
