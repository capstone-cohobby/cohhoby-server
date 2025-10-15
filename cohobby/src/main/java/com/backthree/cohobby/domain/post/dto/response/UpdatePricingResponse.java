package com.backthree.cohobby.domain.post.dto.response;

import com.backthree.cohobby.domain.post.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdatePricingResponse {
    @Schema(description = "생성된 게시물 ID", example = "123")
    private Long postId;
    public static UpdatePricingResponse fromEntity(Post post) {
        return new UpdatePricingResponse(
                post.getId()
        );
    }
}
