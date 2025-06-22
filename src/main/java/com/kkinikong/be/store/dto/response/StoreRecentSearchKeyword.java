package com.kkinikong.be.store.dto.response;

public record StoreRecentSearchKeyword(String keyword) {
  public static StoreRecentSearchKeyword from(String keyword) {
    return new StoreRecentSearchKeyword(keyword);
  }
}
