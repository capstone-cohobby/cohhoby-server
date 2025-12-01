package com.backthree.cohobby.domain.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
public class AiEstimateResponse {
    private Integer suggestedLowPrice;
    private Integer suggestedPointPrice;
    private Integer suggestedHighPrice;
    private Integer suggestedDeposit;
    private String caution;
    private String priceReason;   // 가격 책정 사유
    private String depositReason; // 보증금 책정 사유
    private String ruleReason;    // 규칙 제안 사유
    private String decision;
    private Double confidence;
    private Integer referencePrice; // 기준 가격
    private String referenceUrl;    // 기준 URL
    private String referenceType;   // 기준 유형 (new/used)

    // 💡 추가: 참고 자료 리스트
    private List<EvidenceDto> evidence;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvidenceDto {
        private String title;
        private String url;
    }
}
