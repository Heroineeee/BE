package com.kkinikong.be.cache.type;

import lombok.Getter;

@Getter
public enum RedisKey {
  STORE_VIEWS_KEY("store-views"),
  COMMUNITY_POST_VIEWS_KEY("community-post-views"),
  RECENT_SEARCHES_KEY("recent-searches"),
  STORE_LOCATIONS_KEY("store-locations"),
  ;

  private final String key;

  RedisKey(String key) {
    this.key = key;
  }
}
