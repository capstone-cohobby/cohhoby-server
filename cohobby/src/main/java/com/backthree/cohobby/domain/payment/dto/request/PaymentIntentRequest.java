package com.backthree.cohobby.domain.payment.dto.request;

import com.backthree.cohobby.domain.payment.entity.PaymentMethod;

public record PaymentIntentRequest(
        Long rentId, //대여 건 구별
        Integer amount, //결제 요청 금액
        PaymentMethod method //결제 방법
) {
}
