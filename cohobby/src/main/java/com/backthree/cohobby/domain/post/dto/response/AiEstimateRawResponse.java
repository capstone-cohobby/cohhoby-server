package com.backthree.cohobby.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AiEstimateRawResponse {
    private PriceInfo price;
    private DepositInfo deposit;
    private RuleInfo rules;

    // 추가: AI 응답의 evidence 배열 매핑
    private List<EvidenceInfo> evidence;

    @Getter
    @NoArgsConstructor
    public static class PriceInfo {
        private PriceDetail price;
        private String decision;
        private Double confidence;
        private String reasoning; // 가격 사유

        @Getter
        @NoArgsConstructor
        public static class PriceDetail {
            private Integer point;
            private Integer low;
            private Integer high;
            @JsonProperty("reference_price")
            private Integer referencePrice;
            @JsonProperty("reference_type")
            private String referenceType;
            @JsonProperty("reference_url")
            private String referenceUrl;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class DepositInfo {
        @JsonProperty("deposit_amount")
        private Integer depositAmount;
        @JsonProperty("deposit_required")
        private Boolean depositRequired; // 보증금 필요 여부
        private String reasoning; // 보증금 사유
    }

    @Getter
    @NoArgsConstructor
    public static class RuleInfo {
        private List<String> rules;
        private String reasoning; // 규칙 사유
    }

    //  추가: Evidence 내부 클래스
    @Getter
    @NoArgsConstructor
    public static class EvidenceInfo {
        private String title;
        private String url;
        // doc_id, score 등은 필요 없다면 생략 가능
    }
}
