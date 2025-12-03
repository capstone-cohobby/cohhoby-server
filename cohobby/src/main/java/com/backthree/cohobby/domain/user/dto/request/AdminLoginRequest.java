package com.backthree.cohobby.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AdminLoginRequest(
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        String email
) {
}

