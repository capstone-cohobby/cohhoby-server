package com.backthree.cohobby.domain.like.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteLikeResponse {
    @Schema(description = "게시물 ID", example = "123")
    private Long postId;

    @Schema(description = "메시지", example = "찜이 성공적으로 취소되었습니다.")
    private String message;

    public static DeleteLikeResponse from(Long postId) {
        return DeleteLikeResponse.builder()
                .postId(postId)
                .message("찜이 성공적으로 취소되었습니다.")
                .build();
    }
}