package com.kkinikong.be.convenience.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import com.kkinikong.be.convenience.dto.response.ConvenienceRecommendationResponse;
import com.kkinikong.be.convenience.exception.ConvenienceException;
import com.kkinikong.be.convenience.exception.errorcode.ConvenienceErrorCode;
import com.kkinikong.be.convenience.util.dto.OpenAIRequest;
import com.kkinikong.be.convenience.util.dto.OpenAIResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAIApiClient {

  private final WebClient webClient;

  private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

  @Value("${openai.api.key}")
  private String apiKey;

  public ConvenienceRecommendationResponse getProductNameRecommendation(
      OpenAIRequest openAIRequest) {
    return webClient
        .post()
        .uri(OPENAI_API_URL)
        .header("Authorization", "Bearer " + apiKey)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(openAIRequest)
        .exchangeToMono(
            response -> {
              if (response.statusCode().is2xxSuccessful()) {
                return response
                    .bodyToMono(OpenAIResponse.class)
                    .<ConvenienceRecommendationResponse>handle(
                        (openAIResponse, sink) -> {
                          List<OpenAIResponse.Choice> choices = openAIResponse.getChoices();
                          if (choices != null && !choices.isEmpty()) {
                            String rawContent = choices.get(0).getMessage().getContent();
                            try {
                              List<String> parsed = parseChoices(rawContent);
                              sink.next(new ConvenienceRecommendationResponse(parsed));
                            } catch (JsonProcessingException e) {
                              sink.error(
                                  new ConvenienceException(
                                      ConvenienceErrorCode.PARSE_CHOICES_ERROR));
                            }
                          } else {
                            sink.error(
                                new ConvenienceException(ConvenienceErrorCode.OPEN_AI_API_ERROR));
                          }
                        });
              } else {
                return response
                    .bodyToMono(String.class)
                    .flatMap(
                        errorBody -> {
                          log.error("❌ OpenAI API Error Response: {}", errorBody);
                          return Mono.error(
                              new ConvenienceException(ConvenienceErrorCode.OPEN_AI_API_ERROR));
                        });
              }
            })
        .block();
  }

  private List<String> parseChoices(String rawChoices) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(rawChoices, new TypeReference<>() {});
  }
}
