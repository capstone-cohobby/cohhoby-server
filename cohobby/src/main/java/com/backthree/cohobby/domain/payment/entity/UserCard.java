package com.backthree.cohobby.domain.payment.entity;

import com.backthree.cohobby.domain.common.BaseTimeEntity;
import com.backthree.cohobby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "userId"))
public class UserCard extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false, unique = true)
    private User user;

    @Column(length = 127, nullable = false)
    private String billingKey; // 토스페이먼츠 빌링키

    @Column(length = 63)
    private String cardNumber; // 마스킹된 카드번호 (예: 1234-****-****-5678)

    @Column(length = 31)
    private String cardCompany; // 카드사명

    @Column(length = 31)
    private String cardType; // 카드 타입 (신용카드, 체크카드 등)

    private Boolean isDefault; // 기본 카드 여부

    public void updateCardInfo(String billingKey, String cardNumber, String cardCompany, String cardType) {
        this.billingKey = billingKey;
        this.cardNumber = cardNumber;
        this.cardCompany = cardCompany;
        this.cardType = cardType;
    }
}

