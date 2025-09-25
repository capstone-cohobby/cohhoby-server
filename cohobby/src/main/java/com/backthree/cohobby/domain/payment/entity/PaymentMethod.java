package com.backthree.cohobby.domain.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    CARD("CARD"),
    ACCOUNT("ACCOUNT"),
    VIRTUAL_ACCOUNT("VIRTUAL_ACCOUNT"),
    TRANSFER("TRANSFER"),
    MOBILE("MOBILE"),
    POINT("POINT"),
    ETC("ETC");

    private final String value;
}