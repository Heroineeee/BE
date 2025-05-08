package com.kkinikong.be.store.domain.type;

import lombok.Getter;

@Getter
public enum StoreSort {
  DISTANCE("가까운 순"),
  RATING("별점 높은 순"),
  REVIEW_COUNT("리뷰 많은 순"),
  VIEW_COUNT("조회수 순"),
  NAME("가나다 순");

  private final String label;

  StoreSort(String label) {
    this.label = label;
  }
}
