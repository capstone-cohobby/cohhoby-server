package com.backthree.cohobby.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePricingRequest {

    @Schema(description = "일일 대여 가격", example = "5000")
    @NotNull()
    private Integer dailyPrice;

    @Schema(description = "보증금", example = "10000")
    private Integer deposit;

    @Schema(description = "주의사항", example = "")
    private String caution;

}
