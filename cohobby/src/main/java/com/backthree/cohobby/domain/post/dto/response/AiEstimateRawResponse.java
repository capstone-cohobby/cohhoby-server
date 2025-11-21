package com.backthree.cohobby.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AiEstimateRawResponse {
    private PriceSection price;
    private DepositSection deposit;
    private RulesSection rules;

    @Getter
    @Setter
    public static class PriceSection {
        private String decision;      // "uncertain"
        private Double confidence;    // 0.3
        private String reasoning;     // 가격 관련 이유

        private PriceDetail price;    // 중첩된 price 객체
    }

    @Getter
    @Setter
    public static class PriceDetail {
        private Integer point;        // 단일 추천가
        private Integer low;          // 최저
        private Integer high;         // 최고
        private String basis;         // 산출 근거 텍스트
    }

    @Getter
    @Setter
    public static class DepositSection {
        @JsonProperty("deposit_required")
        private Boolean depositRequired;

        @JsonProperty("deposit_amount")
        private Integer depositAmount;

        private String reasoning;
    }

    @Getter
    @Setter
    public static class RulesSection {
        private List<String> rules;   // 규칙 리스트
        private String reasoning;
    }
}
