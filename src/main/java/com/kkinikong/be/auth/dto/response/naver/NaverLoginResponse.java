package com.kkinikong.be.auth.dto.response.naver;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NaverLoginResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("token_type") String tokenType,
    @JsonProperty("expires_in") String expiresIn) {}
