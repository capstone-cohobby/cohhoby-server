package com.backthree.cohobby.domain.inquiry.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InquiryType {
    PAYMENT("PAYMENT"),
    ACCOUNT("ACCOUNT"),
    APP_BUG("APP_BUG"),
    POLICY("POLICY"),
    GENERAL("GENERAL"),
    OTHER("OTHER");

    private final String value;
}