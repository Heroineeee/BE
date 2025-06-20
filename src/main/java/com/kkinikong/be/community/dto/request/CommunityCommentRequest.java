package com.kkinikong.be.community.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommunityCommentRequest(@NotNull @Size(max = 4000) String content) {}
