package com.kkinikong.be.convenience.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.convenience.repository.ConvenienceRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ConvenienceService {

  private final ConvenienceRepository convenienceRepository;

  public String getProductNameRecommendation(String productName) {

    return "string";
  }
}
