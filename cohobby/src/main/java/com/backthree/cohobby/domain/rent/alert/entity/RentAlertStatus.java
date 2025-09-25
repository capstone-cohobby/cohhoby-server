package com.backthree.cohobby.domain.rent.alert.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RentAlertStatus {
    DELIVERED("DELIVERED"),
    READ("READ"),
    FAILED("FAILED");

    private final String value;
}