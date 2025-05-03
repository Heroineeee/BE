package com.kkinikong.be.batch.processor;

import java.util.Map;

public class CategoryMapper {
  private static final Map<String, String> categoryMap =
      Map.ofEntries(
          Map.entry("제과", "간식"),
          Map.entry("카페", "간식"),
          Map.entry("샐러드", "기타"),
          Map.entry("퓨전요리", "기타"),
          Map.entry("뷔페", "기타"),
          Map.entry("패스트푸드", "양식"),
          Map.entry("패밀리레스토랑", "양식")
          // 나머지는 그대로 사용
          );

  public static String mapToUpperCategory(String label) {
    return categoryMap.getOrDefault(label, label); // 없으면 자기 자신 리턴
  }
}
