package com.kkinikong.be.user.dto.request;

import jakarta.validation.constraints.NotNull;

public record NicknameRequest(@NotNull String nickname) {}
