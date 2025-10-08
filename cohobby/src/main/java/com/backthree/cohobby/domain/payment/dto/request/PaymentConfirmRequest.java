package com.backthree.cohobby.domain.payment.dto.request;

public record PaymentConfirmRequest (
        String orderId, //시스템에서 발급한 주문 번호, pgOrderNo에 대응됨
        String paymentKey, //PG사가 발급한 결제 키
        Integer amount //최종 결제 금액(검증용)
) {
}
