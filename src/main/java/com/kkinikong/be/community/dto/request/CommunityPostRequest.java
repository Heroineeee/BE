package com.kkinikong.be.community.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.kkinikong.be.community.domain.type.Category;

public record CommunityPostRequest(
    @NotNull @Size(min = 5) String title,
    @NotNull @Size(min = 10) String content,
    @NotNull Category category) {}
