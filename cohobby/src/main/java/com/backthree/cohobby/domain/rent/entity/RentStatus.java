package com.backthree.cohobby.domain.rent.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RentStatus {
    CREATED("CREATED"),
    CONFIRMED("CONFIRMED"),
    ONGOING("ONGOING"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED"),
    DISPUTED("DISPUTED");

    private final String value;
}
