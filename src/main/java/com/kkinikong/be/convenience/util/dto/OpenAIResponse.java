package com.kkinikong.be.convenience.util.dto;

import java.util.List;

import lombok.Data;

@Data
public class OpenAIResponse {
  private List<Choice> choices;

  @Data
  public static class Choice {
    private Message message;
    private String finish_reason;
    private int index;
  }

  @Data
  public static class Message {
    private String role;
    private String content;
  }
}
