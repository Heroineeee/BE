package com.kkinikong.be.convenience.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@AllArgsConstructor
public class OpenAIApiClient {

  private static final String MODEL = "gpt-3.5-turbo";
  private static final int MAX_TOKENS = 300;

  @JsonProperty("model")
  private String model;

  @JsonProperty("max_tokens")
  private int maxTokens;

  @JsonProperty("messages")
  private List<Message> messages;

  public static OpenAIApiClient createOpenAIRequest(String productName) {
    return new OpenAIApiClient(
        MODEL,
        MAX_TOKENS,
        List.of(
            new Message(
                "system",
                "You are an AI assistant that suggests accurate product names sold at convenience stores in Korea (e.g., CU, GS25, 7-Eleven). "
                    + "The user may input a vague or abbreviated product name. "
                    + "Your task is to recommend up to 3 actual product names that are most relevant and likely to match what the user intended. "
                    + "Respond only with a JSON array of product names, and make sure the product names are written in natural Korean, including the brand name and volume"
                    + "If the input does not relate to any product or is too vague to match, respond with [\"관련 제품 없음\"] instead. "
                    + "Do not add any explanation, code block, or formatting around the result. Respond strictly in Korean."),
            new Message("user", productName)));
  }
}
