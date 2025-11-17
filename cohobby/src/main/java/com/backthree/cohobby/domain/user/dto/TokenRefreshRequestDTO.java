package com.backthree.cohobby.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRefreshRequestDTO {
    private String refreshToken;
}
