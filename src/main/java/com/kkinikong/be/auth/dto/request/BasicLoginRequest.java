package com.kkinikong.be.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BasicLoginRequest(@Email String email, @NotNull String password) {}
