package com.backthree.cohobby.domain.payment.dto.response;

import com.backthree.cohobby.domain.payment.entity.Payment;
import com.backthree.cohobby.domain.payment.entity.PaymentMethod;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.global.config.payments.PaymentsConfig;
import lombok.Builder;

@Builder
public record PaymentIntentResponse (
        PaymentMethod method,
        Integer amountValue, //결제 금액
        String amountCurrency, //결제 통화 - KRW만 지원
        String orderName, //구매상품
        String pgOrderNo, //주문번호
        String customerName,
        String customerEmail,
        String successUrl,
        String failUrl
        )
{
    public static PaymentIntentResponse from(Payment payment, User user, PaymentsConfig urlConfig) {
        return PaymentIntentResponse.builder()
                .method(payment.getMethod())
                .amountValue(payment.getAmountExpected())
                .amountCurrency(payment.getCurrency())
                .orderName(payment.getOrderName())
                .pgOrderNo(payment.getPgOrderNo())
                .customerName(user.getNickname())
                .customerEmail(user.getEmail())
                .successUrl(urlConfig.getSuccessUrl())
                .failUrl(urlConfig.getFailUrl())
                .build();
    }
}
