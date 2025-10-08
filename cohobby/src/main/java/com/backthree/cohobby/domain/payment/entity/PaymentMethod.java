package com.backthree.cohobby.domain.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    CARD("CARD"),
    EASY_PAY("EASY_PAY"),
    VIRTUAL_ACCOUNT("VIRTUAL_ACCOUNT"),
    MOBILE_PHONE("MOBILE_PHONE"),
    TRANSFER("TRANSFER"),
    CULTURE_GIFT_CERTIFICATE("CULTURE_GIFT_CERTIFICATE"),
    BOOK_GIFT_CERTIFICATE("BOOK_GIFT_CERTIFICATE"),
    GAME_GIFT_CERTIFICATE("GAME_GIFT_CERTIFICATE");

    private final String value;
}