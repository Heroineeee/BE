package com.kkinikong.be.convenience.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.convenience.dto.response.ConvenienceRecommendationResponse;
import com.kkinikong.be.convenience.util.OpenAIApiClient;
import com.kkinikong.be.convenience.util.dto.OpenAIRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ConvenienceService {

  private final OpenAIApiClient openAIApiClient;

  public ConvenienceRecommendationResponse getProductNameRecommendation(String productName) {
    OpenAIRequest openAIRequest = new OpenAIRequest(productName);
    return openAIApiClient.getProductNameRecommendation(openAIRequest);
  }
}
