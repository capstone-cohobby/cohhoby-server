package com.backthree.cohobby.domain.like.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateLikeResponse {
    @Schema(description = "찜 ID", example = "1")
    private Long likeId;

    @Schema(description = "게시물 ID", example = "123")
    private Long postId;

    @Schema(description = "메시지", example = "찜이 성공적으로 생성되었습니다.")
    private String message;

    public static CreateLikeResponse from(Long likeId, Long postId) {
        return CreateLikeResponse.builder()
                .likeId(likeId)
                .postId(postId)
                .message("찜이 성공적으로 생성되었습니다.")
                .build();
    }
}