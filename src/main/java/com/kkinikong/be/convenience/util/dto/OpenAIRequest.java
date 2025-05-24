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
                "You are an AI assistant that suggests accurate product names sold at convenience stores in Korea."
                    + "The user may input a vague or abbreviated product name. "
                    + "Your task is to recommend **exactly 3 actual product names** that are most relevant and likely to match what the user intended. "
                    + "Respond only with a JSON array of product names (e.g., [\"...\"]) written in natural Korean, including brand name and volume. "
                    + "**Do not include the name of the convenience store** in the product names. "
                    + "If the input does not relate to any actual product, or is too vague to match, respond with [\"관련 제품 없음\"]. "
                    + "Do not fabricate product names. If unsure, return [\"관련 제품 없음\"]. "
                    + "Do not add any explanation, markdown, or formatting. Respond strictly in raw JSON, in Korean."),
            new Message("user", productName));
  }
}
