package com.backthree.cohobby.domain.post.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("name")
    private String goods;

    @Schema(description = "구입 시기", example = "2023-05-22")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("bought_at")
    private LocalDate purchaseAt;

    @Schema(description = "상태", example = "실착용 3회")
    @JsonProperty("condition")
    private String defectStatus;
}
