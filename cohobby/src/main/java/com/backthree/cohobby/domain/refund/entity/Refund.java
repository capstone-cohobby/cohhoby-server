package com.backthree.cohobby.domain.refund.entity;

import com.backthree.cohobby.domain.common.BaseTimeEntity;
import com.backthree.cohobby.domain.payment.entity.Payment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Refund extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer amount;

    @Column(length = 3)
    private String currency;

    @Column(columnDefinition = "json")
    private String reason; // JSON → String

    @Enumerated(EnumType.STRING)
    private RefundStatus status;

    @Column(length = 127) private String pgRefundKey;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(length = 63) private String failureCode;
    @Column(length = 255) private String failureMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paymentId", nullable = false)
    private Payment payment;
}
