package com.kkinikong.be.community.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.kkinikong.be.community.domain.type.Category;

public record CommunityPostRequest(
    @NotNull @Size(min = 1, max = 25) String title,
    @NotNull @Size(max = 5000) String content,
    @NotNull Category category) {}
