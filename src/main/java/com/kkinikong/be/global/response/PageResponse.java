package com.kkinikong.be.global.response;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

public record PageResponse<T>(List<T> content, int totalPage, int currentPage, long totalCount) {
  public static <T> PageResponse<T> of(List<T> content, int totalPage, int currentPage) {
    return new PageResponse<>(content, totalPage, currentPage, content.size());
  }

  public static <T> PageResponse<T> of(
      List<T> content, int totalPage, int currentPage, long totalCount) {
    return new PageResponse<>(content, totalPage, currentPage, totalCount);
  }

  public static <T, E> PageResponse<E> from(Page<T> page, Function<T, E> converter) {
    List<E> mapped = page.getContent().stream().map(converter).toList();
    return new PageResponse<>(
        mapped, page.getTotalPages(), page.getNumber(), page.getTotalElements());
  }
}
