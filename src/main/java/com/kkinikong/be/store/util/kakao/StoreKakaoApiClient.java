package com.kkinikong.be.store.util.kakao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.exception.StoreException;
import com.kkinikong.be.store.exception.errorcode.StoreErrorCode;

@Slf4j
@RequiredArgsConstructor
@Component
public class StoreKakaoApiClient {
  private final WebClient webClient = WebClient.builder().baseUrl("https://dapi.kakao.com").build();

  @Value("${KAKAO_REST_API_KEY}")
  private String kakaoApiKey;

  public String getKakaoLocalSearch(Store store) {
    String response =
        webClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/v2/local/search/keyword.json")
                        .queryParam("category_group_code", "FD6")
                        .queryParam("size", 1)
                        .queryParam("x", store.getLatitude())
                        .queryParam("y", store.getLongitude())
                        .queryParam("query", store.getName())
                        .build())
            .header("Authorization", "KakaoAK " + kakaoApiKey)
            .retrieve()
            .bodyToMono(String.class)
            .block();

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode root = objectMapper.readTree(response);
      JsonNode results = root.path("documents");
      if (!results.isEmpty()) {
        return results.get(0).path("id").asText(); // 첫번째 결과의 id
      } else {
        return null; // 결과 없을 때
      }
    } catch (Exception e) {
      throw new StoreException(StoreErrorCode.KAKAO_API_PARSE_ERROR);
    }
  }
}
