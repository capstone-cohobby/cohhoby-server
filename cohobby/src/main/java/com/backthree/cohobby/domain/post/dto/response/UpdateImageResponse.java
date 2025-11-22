package com.backthree.cohobby.domain.post.dto.response;

import com.backthree.cohobby.domain.post.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UpdateImageResponse {
    @Schema(description = "생성된 게시물 ID", example = "123")
    private List<String> imageUrls;
    public static UpdateImageResponse from(List<String> imageUrls) {
        return UpdateImageResponse.builder()
                        .imageUrls(imageUrls)
                .build();
    }
}
