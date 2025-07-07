package com.kkinikong.be.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NicknameRequest(
    @NotBlank(message = "닉네임은 필수 입력입니다.") @Size(max = 7, message = "닉네임은 공백 포함 최대 7자까지 입력할 수 있습니다.")
        String nickname) {}
