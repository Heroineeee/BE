package com.kkinikong.be.global.response;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public record PageResponse<T>(List<T> content, int totalPage, int currentPage) {
  public static <T> PageResponse<T> of(List<T> content, int totalPage, int currentPage) {
    return new PageResponse<T>(content, totalPage, currentPage);
  }

  public static <T, E> PageResponse<E> from(Page<T> page, Function<T, E> converter) {
    List<E> mapped = page.getContent().stream().map(converter).toList();
    return new PageResponse<>(mapped, page.getTotalPages(), page.getNumber());
  }

  public static <T> PageResponse<T> empty(Pageable pageable) {
    return new PageResponse<>(List.of(), 0, pageable.getPageNumber());
  }
}
