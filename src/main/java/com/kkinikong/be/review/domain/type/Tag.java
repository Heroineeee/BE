package com.kkinikong.be.review.domain.type;

import lombok.Getter;

@Getter
public enum Tag {
  // 메뉴 관련
  TASTE_GOOD("음식이 맛있어요"),
  INGREDIENT_FRESH("재료가 신선해요"),
  GOOD_FOR_KIDS("아이들이 먹기 좋아요"),
  MENU_VARIETY("메뉴가 다양해요"),

  // 공간 관련
  GOOD_FOR_SOLO("혼자 가도 편해요"),
  NICE_ATMOSPHERE("분위기가 좋아요"),
  GOOD_FOR_TALK("이야기하기 좋아요"),
  CLEAN("매장이 청결해요"),
  QUICK_SERVING("금방 나와요"),

  // 서비스 관련
  KIND_STAFF("직원이 친절해요"),
  NO_PAYMENT_ISSUE("결제 거절이 없어요"),
  COMFORTABLE_MEAL("편하게 먹을 수 있어요"),

  // 기타
  TAKEOUT_AVAILABLE("포장 가능해요"),
  GOOD_PARKING("주차하기 편해요"),
  ;

  private final String label;

  Tag(String label) {
    this.label = label;
  }
}
