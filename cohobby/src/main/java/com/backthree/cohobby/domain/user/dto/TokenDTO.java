package com.backthree.cohobby.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenDTO {
    private final String accessToken;
    private final String refreshToken;

    @Builder
    public TokenDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
