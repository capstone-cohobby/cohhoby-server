package com.backthree.cohobby.domain.rent.alert.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RentAlertType {
    PICKUP("PICKUP"),
    RETURN("RETURN"),
    PAYMENT_DUE("PAYMENT_DUE"),
    OTHER("OTHER");

    private final String value;
}
