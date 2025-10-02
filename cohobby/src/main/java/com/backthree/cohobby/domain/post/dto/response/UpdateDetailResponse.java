package com.backthree.cohobby.domain.post.dto.response;

import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.post.entity.PostStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateDetailResponse {
    @Schema(description = "생성된 게시물 ID", example = "123")
    private Long postId;

    public static UpdateDetailResponse fromEntity(Post post) {
        return new UpdateDetailResponse(
                post.getId()
        );
    }
}
