package com.kkinikong.be.cache.type;

import lombok.Getter;

@Getter
public enum RedisKey {
  STORE_VIEWS_KEY("store-views"),
  COMMUNITY_POST_VIEWS_KEY("community-post-views");

  private final String key;

  RedisKey(String key) {
    this.key = key;
  }
}
