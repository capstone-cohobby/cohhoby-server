package com.backthree.cohobby.domain.report.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportType {
    // 보증금 자동결제 관련 신고 (빌려준 사람이 신고)
    MINOR_DAMAGE("MINOR_DAMAGE"), // 경미파손
    DAMAGE("DAMAGE"), // 파손
    RETURN_DELAY("RETURN_DELAY"), // 반납 연체
    // 양방향 신고 (자동결제 없음)
    ITEM_DEFECT("ITEM_DEFECT"), // 설명과 다름 (기존)
    NOT_AS_DESCRIBED("NOT_AS_DESCRIBED"), // 설명과 다름
    SCAM("SCAM"), // 사기
    PROHIBITED_ITEM("PROHIBITED_ITEM"), // 금지물품
    ABUSE("ABUSE"), // 욕설
    OTHER("OTHER"); // 기타

    private final String value;
    
    // 보증금 자동결제가 필요한 신고 유형인지 확인
    public boolean requiresAutoPayment() {
        return this == MINOR_DAMAGE || this == DAMAGE || this == RETURN_DELAY || this == OTHER;
    }
}
