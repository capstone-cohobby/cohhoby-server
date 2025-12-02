package com.backthree.cohobby.domain.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentType {
    RENTAL_FEE("RENTAL_FEE"), // 대여료
    DEPOSIT("DEPOSIT"); // 보증금

    private final String value;
}

