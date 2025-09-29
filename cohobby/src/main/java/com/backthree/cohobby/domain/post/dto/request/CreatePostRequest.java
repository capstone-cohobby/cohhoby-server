package com.backthree.cohobby.domain.post.dto.request;


import com.backthree.cohobby.domain.category.entity.Category;
import com.backthree.cohobby.domain.hobby.entity.Hobby;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class CreatePostRequest {
    @Schema(description = "상품명", example = "나이키 에어포스")
    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String goods;

    @Schema(description= "userId", example = "1")
    @NotNull()
    private Long userId;

    @Schema(description = "취미 ID", example = "5")
    @NotNull(message = "취미는 필수 선택 값입니다.")
    private Long hobbyId;
}
