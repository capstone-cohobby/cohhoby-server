package com.backthree.cohobby.domain.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiEstimateResponse {
    @Schema(description = "AI가 추천한 최소 가격")
    private Integer suggestedLowPrice;

    @Schema(description = "AI가 추천한 기준(포인트) 가격")
    private Integer suggestedPointPrice;

    @Schema(description = "AI가 추천한 최대 가격")
    private Integer suggestedHighPrice;

    @Schema(description = "AI가 추천한 보증금 (없으면 null)")
    private Integer suggestedDeposit;

    @Schema(description = "대여 규칙")
    private String caution;

    @Schema(description = "AI가 가격을 이렇게 추천한 이유 설명")
    private String reason;

    @Schema(description = "최종 판단 정도")
    private String decision;

    @Schema(description = "confidence 점수")
    private Double confidence;
}
