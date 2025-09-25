package com.backthree.cohobby.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    MALE("MALE"),
    FEMALE("FEMALE");

    private final String value;
}
