package com.kkinikong.be.review.dto.request;

import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.kkinikong.be.review.domain.type.Tag;

public record ReviewRequest(
    @NotNull
        @Min(value = 1, message = "별점은 1 이상이어야 합니다.")
        @Max(value = 5, message = "별점은 5 이하여야 합니다.")
        int rating,
    @Size(max = 5, message = "태그는 최대 5개까지 선택할 수 있습니다.") List<Tag> tag,
    @Nullable @Size(max = 500, message = "텍스트는 500자까지 입력가능합니다. ") String content) {}
