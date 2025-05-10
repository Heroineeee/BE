package com.kkinikong.be.review.domain.type;

import lombok.Getter;

@Getter
public enum Tag {
  // 메뉴 관련
  TASTE_GOOD("음식이 맛있어요", TagCategory.MENU),
  INGREDIENT_FRESH("재료가 신선해요", TagCategory.MENU),
  GOOD_FOR_KIDS("아이들이 먹기 좋아요", TagCategory.MENU),
  MENU_VARIETY("메뉴가 다양해요", TagCategory.MENU),

  // 공간 관련
  GOOD_FOR_SOLO("혼자 가도 편해요", TagCategory.SPACE),
  NICE_ATMOSPHERE("분위기가 좋아요", TagCategory.SPACE),
  GOOD_FOR_TALK("이야기하기 좋아요", TagCategory.SPACE),
  CLEAN("매장이 청결해요", TagCategory.SPACE),
  QUICK_SERVING("금방 나와요", TagCategory.SPACE),

  // 서비스 관련
  KIND_STAFF("직원이 친절해요", TagCategory.PAYMENT),
  NO_PAYMENT_ISSUE("결제 거절이 없어요", TagCategory.PAYMENT),
  COMFORTABLE_MEAL("편하게 먹을 수 있어요", TagCategory.PAYMENT),

  // 기타
  TAKEOUT_AVAILABLE("포장 가능해요", TagCategory.ETC),
  GOOD_PARKING("주차하기 편해요", TagCategory.ETC),
  ;

  private final String label;
  private final TagCategory category;

  Tag(String label, TagCategory category) {
    this.label = label;
    this.category = category;
  }
}
