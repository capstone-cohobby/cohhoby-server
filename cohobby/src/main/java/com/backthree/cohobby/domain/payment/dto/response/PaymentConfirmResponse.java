package com.backthree.cohobby.domain.payment.dto.response;

import com.backthree.cohobby.domain.payment.entity.Payment;
import com.backthree.cohobby.domain.payment.entity.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentConfirmResponse(
        Long paymentId,
        String pgOrderNo,
        Integer amountCaptured,
        PaymentStatus status,
        LocalDateTime capturedAt,
        LocalDateTime authorizedAt
) {
    public static PaymentConfirmResponse from(Payment payment){
        return new PaymentConfirmResponse(
                payment.getId(),
                payment.getPgOrderNo(),
                payment.getAmountCaptured(),
                payment.getStatus(),
                payment.getCapturedAt(),
                payment.getAuthorizedAt()
        );
    }
}
