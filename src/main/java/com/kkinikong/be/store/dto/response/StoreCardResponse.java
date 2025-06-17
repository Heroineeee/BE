package com.kkinikong.be.store.dto.response;

import java.util.Arrays;

import com.kkinikong.be.store.domain.Store;

public record StoreCardResponse(
    Long id, String name, String address, long viewCount, String category) {
  public static StoreCardResponse from(Store store) {
    String[] addressParts = store.getAddress().split(" ");
    String refinedAddress =
        String.join(" ", Arrays.copyOfRange(addressParts, 2, addressParts.length));

    return new StoreCardResponse(
        store.getId(),
        store.getName(),
        refinedAddress,
        store.getViewCount(),
        store.getCategory().getLabel());
  }
}
