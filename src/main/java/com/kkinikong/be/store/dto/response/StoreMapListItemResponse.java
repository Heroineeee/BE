package com.kkinikong.be.store.dto.response;

import java.text.DecimalFormat;

import com.kkinikong.be.store.domain.Store;

public record StoreMapListItemResponse(
    Long id,
    String name,
    String address,
    double latitude,
    double longitude,
    String category,
    String ratingAvg,
    long scrapCount,
    Boolean isScrapped) {
  public static StoreMapListItemResponse from(Store store) {
    return new StoreMapListItemResponse(
        store.getId(),
        store.getName(),
        store.getAddress(),
        store.getLatitude(),
        store.getLongitude(),
        store.getCategory().getLabel(),
        new DecimalFormat("#.##").format(store.getRatingAvg()),
        store.getScrapCount(),
        store.getIsScrapped());
  }
}
