package com.backthree.cohobby.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePricingRequest {
    @Schema(description = "일일 대여 가격", example = "5000원")
    @NotNull()
    private Integer dailyPrice;

    @Schema(description = "주간 대여 가격", example = "5000원")
    @NotNull()
    private Integer weeklyPrice;

    @Schema(description = "보증금", example = "10000원")
    private Integer deposit;

}
