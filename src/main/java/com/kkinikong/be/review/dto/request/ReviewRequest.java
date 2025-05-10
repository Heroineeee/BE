package com.kkinikong.be.review.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.kkinikong.be.review.domain.type.Tag;

public record ReviewRequest(
    @NotNull int rating,
    @Size(max = 5, message = "태그는 최대 5개까지 선택할 수 있습니다.") Tag[] tag,
    @Nullable String content) {}
