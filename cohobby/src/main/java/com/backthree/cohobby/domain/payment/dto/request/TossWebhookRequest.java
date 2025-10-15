package com.backthree.cohobby.domain.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TossWebhookRequest {
    private String eventType; //PAYMENT_APPROVED, PAYMENT_CANCELED 등
    private String paymentKey; //토스에서 발급하는 결제 키
    private String orderId; //pgOrderNo
    private String status;
    private String approvedAt;
}
