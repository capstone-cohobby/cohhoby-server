package com.backthree.cohobby.domain.report.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportType {
    ITEM_DEFECT("ITEM_DEFECT"),
    NOT_AS_DESCRIBED("NOT_AS_DESCRIBED"),
    SCAM("SCAM"),
    PROHIBITED_ITEM("PROHIBITED_ITEM"),
    ABUSE("ABUSE"),
    OTHER("OTHER");

    private final String value;
}
