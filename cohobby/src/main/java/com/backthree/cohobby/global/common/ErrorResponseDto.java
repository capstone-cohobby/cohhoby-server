package com.backthree.cohobby.global.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "ErrorResponse")
public class ErrorResponseDto {
    @JsonProperty("isSuccess")
    @Schema(description = "성공 여부", example = "false")
    private boolean isSuccess;
    @Schema(description = "에러 코드", example = "POST_NOT_FOUND")
    private String code;
    @Schema(description = "에러 메시지", example = "게시글을 찾을 수 없습니다.")
    private String message;
}
