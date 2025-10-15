package com.backthree.cohobby.domain.payment.dto.response;

import com.backthree.cohobby.domain.payment.entity.Payment;
import com.backthree.cohobby.domain.payment.entity.PaymentMethod;
import com.backthree.cohobby.domain.user.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PaymentDetailResponse (
    Long paymentId,
    PaymentMethod paymentMethod,
    Integer amountCaptured,
    LocalDateTime capturedAt,
    Long rentId,
    String orderName,
    String customerName
)
{
    public static PaymentDetailResponse from(Payment payment, User user) {
        return new PaymentDetailResponse(
                payment.getId(),
                payment.getMethod(),
                payment.getAmountCaptured(),
                payment.getCapturedAt(),
                payment.getRent().getId(),
                payment.getOrderName(),
                user.getNickname()
        );
    }
}
