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

    @Setter
    private String orderName; //결제의도 생성 api 응답 위해 추가

    @OneToMany(mappedBy = "payment")
    private List<Refund> refunds = new ArrayList<>();

    // payment-refund 양방향 정의를 위해 추가
    public void addRefund(Refund refund) {
        this.refunds.add(refund);
    }

    // 결제 승인 완료 처리
    public void confirmSuccess(String pgPaymentKey, LocalDateTime capturedAt, String provider) {
        this.status = PaymentStatus.CAPTURED;
        this.pgPaymentKey = pgPaymentKey;
        this.amountCaptured = this.amountExpected;
        this.capturedAt = capturedAt;
        this.authorizedAt = capturedAt;
        this.provider = provider;
        this.failureCode = null;
        this.failureMessage = null;
    }

    // 결제 실패 처리
    public void confirmFailure(String failureCode, String failureMessage) {
        this.status = PaymentStatus.FAILED;
        this.failureCode = failureCode;
        this.failureMessage = failureMessage;
    }

    // 환불로 상태 변경
    public void toRefunded(){
        this.status = PaymentStatus.REFUNDED;
    }
}
