package com.backthree.cohobby.domain.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
    PENDING("PENDING"),
    AUTHORIZED("AUTHORIZED"),
    CAPTURED("CAPTURED"),
    REFUND_REQUESTED("REFUND_REQUESTED"),
    REFUNDED("REFUNDED"),
    CANCELLED("CANCELLED"),
    FAILED("FAILED");

    private final String value;
}
