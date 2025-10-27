package com.backthree.cohobby.domain.rent.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RentStatus {
    CREATED("CREATED"), //rent 생성 당시 기본값
    CONFIRMED("CONFIRMED"), // 대여 확정 (결제 완료)
    ONGOING("ONGOING"), // 현재 대여 기간 중에 있음
    COMPLETED("COMPLETED"), // 대여 기간이 끝남
    CANCELLED("CANCELLED"), // 취소됨, 결제까지 가지 않음
    DISPUTED("DISPUTED"); //분쟁 상태

    private final String value;
}
