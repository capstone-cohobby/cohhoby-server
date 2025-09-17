package com.backthree.cohobby.domain.rent.alert.entity;

import com.backthree.cohobby.domain.common.BaseTimeEntity;
import com.backthree.cohobby.domain.rent.entity.Rent;
import com.backthree.cohobby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentAlert extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RentAlertStatus status;

    @Enumerated(EnumType.STRING)
    private RentAlertType type;

    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private LocalDateTime respondedAt;

    @Column(columnDefinition = "varchar(31)")
    private String response; // ENUM → String

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rentId", nullable = false)
    private Rent rent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}