package com.kkinikong.be.community.dto.request;

import jakarta.validation.constraints.NotNull;

public record CommunityCommentRequest(@NotNull String content) {}
