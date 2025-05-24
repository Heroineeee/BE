package com.kkinikong.be.convenience.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.kkinikong.be.convenience.domain.type.Brand;
import com.kkinikong.be.convenience.domain.type.Category;

public record ConvenienceRequest(
    @NotBlank(message = "제품명을 입력해주세요.") String name,
    @NotNull(message = "편의점 이름을 선택해주세요.") Brand brand,
    @NotNull(message = "카테고리를 선택해주세요.") Category category,
    @Size(max = 300, message = "공유하고 싶은 정보를 선택적으로 입력해주세요. (공백 포함 최대 300자까지)") String description,
    @NotNull(message = "결제 가능 여부를 선택해주세요.") Boolean isAvailable) {}
