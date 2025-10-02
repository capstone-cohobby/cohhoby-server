package com.backthree.cohobby.global.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponseDto {
    @JsonProperty("isSuccess")
    private boolean isSuccess;
    private String code;
    private String message;
}
