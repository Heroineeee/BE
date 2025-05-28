package com.kkinikong.be.user.dto.request;

import jakarta.validation.constraints.NotNull;

public record UserPlaceRequest(@NotNull Double latitude, @NotNull Double longitude) {}
