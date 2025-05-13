package com.kkinikong.be.store.util.kakao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.store.exception.StoreException;
import com.kkinikong.be.store.exception.errorcode.StoreErrorCode;

@Service
@RequiredArgsConstructor
public class StoreReverseGeocoding {

  private final WebClient webClient = WebClient.builder().baseUrl("https://dapi.kakao.com").build();

  @Value("${KAKAO_REST_API_KEY}")
  private String kakaoApiKey;

  public String getRegionFromCoordinates(double latitude, double longitude) {
    String response =
        webClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/v2/local/geo/coord2regioncode.json")
                        .queryParam("x", longitude)
                        .queryParam("y", latitude)
                        .build())
            .header("Authorization", "KakaoAK " + kakaoApiKey)
            .retrieve()
            .bodyToMono(String.class)
            .block();

    return parsingRegion(response);
  }

  private static String parsingRegion(String response) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode root = objectMapper.readTree(response);
      JsonNode results = root.path("documents");
      if (!results.isEmpty()) {
        return results.get(0).path("address_name").asText(); // 예: "인천광역시 서구"
      } else {
        return null;
      }
    } catch (Exception e) {
      throw new StoreException(StoreErrorCode.KAKAO_API_PARSE_ERROR);
    }
  }
}
