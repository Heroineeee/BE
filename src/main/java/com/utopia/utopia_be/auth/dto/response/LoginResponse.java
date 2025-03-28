package com.utopia.utopia_be.auth.dto.response;

import lombok.Builder;

import com.utopia.utopia_be.user.domain.type.Role;

@Builder
public record LoginResponse(String nickname, Role role, String accessToken) {}
