package com.backthree.cohobby.domain.inquiry.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InquiryStatus {
    OPEN("OPEN"),
    ANSWERED("ANSWERED"),
    CLOSED("CLOSED");

    private final String value;
}
