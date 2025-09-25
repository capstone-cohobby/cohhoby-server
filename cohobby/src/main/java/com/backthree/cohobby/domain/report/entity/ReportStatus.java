package com.backthree.cohobby.domain.report.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportStatus {
    OPEN("OPEN"),
    IN_PROGRESS("IN_PROGRESS"),
    RESOLVED("RESOLVED"),
    REJECTED("REJECTED"),
    CLOSED("CLOSED");

    private final String value;
}