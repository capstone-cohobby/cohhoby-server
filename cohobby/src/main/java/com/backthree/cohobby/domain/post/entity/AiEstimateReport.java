package com.backthree.cohobby.domain.post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiEstimateReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="post_id")
    private Post post;
    private Integer suggestedLowPrice;
    private Integer suggestedPointPrice;
    private Integer suggestedHighPrice;
    private Integer suggestedDeposit;
    @Lob
    private String caution;
    private String priceReason;
    private String depositReason;
    private String ruleReason;
    private String decision;
    private Double confidence;
    private Integer referencePrice;
    private String referenceUrl;
    private String referenceType;

    @Lob
    private String evidenceJson; // 참고자료는 JSON 문자열로 저장
}
