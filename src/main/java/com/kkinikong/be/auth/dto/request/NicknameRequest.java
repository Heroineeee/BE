package com.kkinikong.be.auth.dto.request;

import jakarta.validation.constraints.NotNull;

public record NicknameRequest(@NotNull String nickname) {}
