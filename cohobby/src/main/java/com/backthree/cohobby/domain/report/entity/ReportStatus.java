package com.backthree.cohobby.domain.report.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportStatus {
    OPEN("OPEN"), // 신고 접수
    IN_PROGRESS("IN_PROGRESS"), // 처리 중
    APPROVED("APPROVED"), // 관리자 승인 (자동결제 실행)
    RESOLVED("RESOLVED"), // 해결 완료
    REJECTED("REJECTED"), // 거부됨
    CLOSED("CLOSED"); // 종료

    private final String value;
}