package com.backthree.cohobby.domain.payment.entity;

import com.backthree.cohobby.domain.common.BaseTimeEntity;
import com.backthree.cohobby.domain.refund.entity.Refund;
import com.backthree.cohobby.domain.rent.entity.Rent;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 31) private String provider;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Column(length = 3) private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private Integer amountExpected;
    private Integer amountCaptured;
    private Integer amountAuthorized;

    @Column(length = 127) private String pgPaymentKey;
    @Column(length = 127) private String pgOrderNo;

    @Column(length = 63) private String failureCode;
    @Column(length = 255) private String failureMessage;

    private LocalDateTime authorizedAt;
    private LocalDateTime capturedAt;
    private LocalDateTime canceledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rentId", nullable = false)
    private Rent rent;

    @OneToMany(mappedBy = "payment")
    private List<Refund> refunds = new ArrayList<>();
}
