package com.kkinikong.be.store.dto.response;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

public record PagedResponse<T>(List<T> content, int totalPage, int currentPage) {
  public static <T> PagedResponse<T> of(List<T> content, int totalPage, int currentPage) {
    return new PagedResponse<T>(content, totalPage, currentPage);
  }

  public static <T, E> PagedResponse<E> from(Page<T> page, Function<T, E> converter) {
    List<E> mapped = page.getContent().stream().map(converter).toList();
    return new PagedResponse<>(mapped, page.getTotalPages(), page.getNumber());
  }
}
