package com.kkinikong.be.store.dto.response;

import lombok.Builder;

@Builder
public record StoreExternalLinkResponse(
    String menuUrl, String directionUrlMobile, String directionUrlDesktop) {}
