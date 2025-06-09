package com.kkinikong.be.commuity.domain.type;

import lombok.Getter;

@Getter
public enum Category {
  WELFARE_INFO("복지정보"),
  CHITCHAT("잡담해요"),
  PARENTING("양육/육아"),
  QUESTION_HELP("문의/도움"),
  LIFE_TIP("생활꿀팁"),
  APPRECIATION("칭찬/감사"),
  ETC("기타");
  private final String label;

  Category(String label) {
    this.label = label;
  }
}
