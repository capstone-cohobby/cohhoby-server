package com.backthree.cohobby.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AiEstimateRequest {
    @Schema(description = "상품명", example="나이키 에어포스")
    @NotBlank(message = "상품명은 필수 입력 값입니다")
    private String goods;

    @Schema(description = "구입 시기", example = "2023-05-22")
    private LocalDate purchaseAt;

    @Schema(description = "상태", example = "")
    private String defectStatus;
}
