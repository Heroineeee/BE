package com.kkinikong.be.store.dto.response;

import java.text.DecimalFormat;

import jakarta.annotation.Nullable;

import com.kkinikong.be.review.domain.type.Tag;
import com.kkinikong.be.store.domain.Store;

public record StoreListItemResponse(
    Long id,
    String name,
    String address,
    String category,
    String ratingAvg,
    long scrapCount,
    @Nullable String representativeTag,
    Boolean isScrapped) {

  public static StoreListItemResponse from(Store store, Tag representativeTag) {
    return new StoreListItemResponse(
        store.getId(),
        store.getName(),
        store.getAddress(),
        store.getCategory().getLabel(),
        new DecimalFormat("#.##").format(store.getRatingAvg()),
        store.getScrapCount(),
        representativeTag == null ? null : representativeTag.getLabel(),
        store.getIsScrapped());
  }
}
