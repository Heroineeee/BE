package com.kkinikong.be.store.dto.response;

public record StoreScrapResponse(Boolean isScrapped, long scrapCount) {
  public static StoreScrapResponse of(Boolean isScrapped, long scrapCount) {
    return new StoreScrapResponse(isScrapped, scrapCount);
  }
}
