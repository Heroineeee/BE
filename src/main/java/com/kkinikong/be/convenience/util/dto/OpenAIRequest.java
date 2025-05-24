package com.kkinikong.be.convenience.util.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OpenAIRequest {
  private static final String MODEL = "gpt-3.5-turbo";
  private static final int MAX_TOKENS = 500;

  @JsonProperty("model")
  private String model;

  @JsonProperty("max_tokens")
  private int maxTokens;

  @JsonProperty("messages")
  private List<Message> messages;

  @JsonProperty("temperature")
  private double temperature;

  @JsonProperty("top_p")
  private double topP;

  public OpenAIRequest(String productName) {
    this.model = MODEL;
    this.maxTokens = MAX_TOKENS;
    this.temperature = 0.2;
    this.topP = 0.7;
    this.messages =
        List.of(
            new Message(
                "system",
                "당신은 대한민국의 편의점(예: CU, GS25, 세븐일레븐 등)에서 판매되는 실제 상품명을 추천하는 AI 어시스턴트입니다. "
                    + "사용자는 줄임말이나 모호한 상품명을 입력할 수 있습니다. "
                    + "당신의 임무는 사용자가 의도한 제품과 가장 유사하고 실제 존재하는 상품명을 **정확히 3개** 추천하는 것입니다. "
                    + "응답은 **브랜드명과 용량**을 포함한 정확한 한국어 상품명으로 이루어진 JSON 배열 형식([\"...\"])으로만 작성하세요. "
                    + "상품명에는 **편의점 이름을 포함하지 마세요.** "
                    + "입력값이 실제 제품과 관련이 없거나 너무 모호한 경우에는 [\"관련 제품 없음\"] 으로 응답하세요. "
                    + "실제 존재하지 않는 상품명을 만들어내지 마세요. 확실하지 않으면 [\"관련 제품 없음\"]으로 응답하세요. "
                    + "설명이나 마크다운, 코드블록 등의 추가 포맷 없이, 오직 순수 JSON 형식으로만 한국어로 응답하세요."),
            new Message("user", productName));
  }
}
