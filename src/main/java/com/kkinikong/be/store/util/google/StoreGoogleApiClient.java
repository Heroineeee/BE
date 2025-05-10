package com.kkinikong.be.store.util.google;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.exception.StoreException;
import com.kkinikong.be.store.exception.errorcode.StoreErrorCode;

@Slf4j
@Service
public class StoreGoogleApiClient {

  @Value("${GOOGLE_API_KEY}")
  private String googleApiKey;

  private static final String PLACES_API_URL = "https://places.googleapis.com/v1/places:searchText";

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  public StoreGoogleApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  @Cacheable(value = "store-opening-hours", key = "#store.id")
  public Map<String, List<String>> getStoreOpeningHours(Store store) {
    ResponseEntity<String> response = sendRequestToGoogle(store);

    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
      return parseGoogleResponse(response.getBody());
    } else {
      throw new StoreException(StoreErrorCode.GOOGLE_API_ERROR);
    }
  }

  private Map<String, List<String>> parseGoogleResponse(String responseBody) {
    try {
      Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
      List<Map<String, Object>> places = (List<Map<String, Object>>) responseMap.get("places");

      if (places != null && !places.isEmpty()) {
        return extractOpeningHours(places.get(0));
      }
    } catch (Exception e) {
      throw new StoreException(StoreErrorCode.JSON_PARSE_ERROR);
    }
    return null;
  }

  private Map<String, List<String>> extractOpeningHours(Map<String, Object> place) {
    Map<String, Object> openingHours = (Map<String, Object>) place.get("currentOpeningHours");

    if (openingHours != null && openingHours.containsKey("weekdayDescriptions")) {
      List<String> weekdayDescriptions = (List<String>) openingHours.get("weekdayDescriptions");
      return (weekdayDescriptions != null && !weekdayDescriptions.isEmpty())
          ? parseOpeningHours(weekdayDescriptions)
          : null;
    }
    return null;
  }

  private ResponseEntity<String> sendRequestToGoogle(Store store) {
    log.info("Google API request for store: {}", store.getName());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("X-Goog-FieldMask", "places.currentOpeningHours");

    Map<String, Object> body = new HashMap<>();
    body.put("textQuery", store.getRegion() + " " + store.getName());
    body.put("languageCode", "ko");
    body.put("regionCode", "KR");
    body.put("maxResultCount", 1);

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
    String urlWithKey = PLACES_API_URL + "?key=" + googleApiKey;
    return restTemplate.postForEntity(urlWithKey, entity, String.class);
  }

  private Map<String, List<String>> parseOpeningHours(List<String> openingHours) {
    Map<String, List<String>> parsedOpeningHours = new LinkedHashMap<>();

    openingHours.forEach(
        description -> {
          String[] parts = description.split(": ", 2);
          if (parts.length < 2) return;

          String dayName = parts[0].substring(0, 1);
          String timePart = parts[1];

          if (timePart.contains("휴무")) {
            parsedOpeningHours.put(dayName, null);
          } else {
            String[] times = timePart.split(" ~ ");
            List<String> convertedTimes =
                Arrays.stream(times)
                    .map(time -> convertTo24Hour(time.trim()))
                    .collect(Collectors.toList());
            parsedOpeningHours.put(dayName, convertedTimes);
          }
        });

    return parsedOpeningHours;
  }

  private String convertTo24Hour(String time) {
    try {
      boolean isAm = time.contains("오전");
      boolean isPm = time.contains("오후");

      time = time.replace("오전", "").replace("오후", "").trim();

      String[] timeParts = time.split(":");
      int hour = Integer.parseInt(timeParts[0].trim());
      int minute = Integer.parseInt(timeParts[1].trim());

      if (isPm && hour != 12) {
        hour += 12;
      }
      if (isAm && hour == 12) {
        hour = 0;
      }

      return String.format("%02d:%02d", hour, minute);
    } catch (Exception e) {
      throw new StoreException(StoreErrorCode.CONVERT_TIME_ERROR);
    }
  }
}
