package com.backthree.cohobby.domain.rent.entity;

import com.backthree.cohobby.domain.common.BaseTimeEntity;
import com.backthree.cohobby.domain.payment.entity.Payment;
import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.rent.alert.entity.RentAlert;
import com.backthree.cohobby.domain.report.entity.Report;
import com.backthree.cohobby.domain.review.entity.Review;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.chatting.entity.ChattingRoom;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rent extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startAt;
    private LocalDateTime duedate;

    @Lob
    private String rule;

    @Enumerated(EnumType.STRING)
    private RentStatus status;

    private Integer totalPrice;

    private Integer dailyPrice;

    @Column(length = 3)
    private String currency;

    @Column(columnDefinition = "json")
    private String cancelPolicy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrowerId", nullable = false)
    private User borrower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", nullable = false)
    private Post post;


    @OneToMany(mappedBy = "rent")
    private Set<Payment> payments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "rent")
    private Set<RentAlert> rentAlerts = new LinkedHashSet<>();

    @OneToMany(mappedBy = "rent")
    private Set<Report> reports = new LinkedHashSet<>();

    @OneToMany(mappedBy = "rent")
    private Set<Review> reviews = new LinkedHashSet<>();

    // 변경 허용 메서드
    public void updateDates(LocalDateTime startAt, LocalDateTime duedate) {
        this.startAt = startAt;
        this.duedate = duedate;
    }

    public void updateRule(String rule) {
        this.rule = rule;
    }

    public void updateDailyPrice(Integer dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public void updateTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void updateStatus(RentStatus status) {
        this.status = status;
    }

    // totalPrice 계산 및 업데이트
    public void calculateAndUpdateTotalPrice() {
        if (this.dailyPrice != null && this.startAt != null && this.duedate != null) {
            // 대여 일수 계산 (시작일과 종료일 포함)
            long days = ChronoUnit.DAYS.between(
                this.startAt.toLocalDate(),
                this.duedate.toLocalDate()
            ) + 1;
            
            // totalPrice = 일일 대여료 * 대여 일수
            this.totalPrice = (int) (this.dailyPrice * days);
        } else {
            // 날짜나 일일 대여료가 없으면 totalPrice를 null로 설정하지 않음 (기존 값 유지)
            // 만약 명시적으로 null로 설정하려면 주석을 해제하세요
            // this.totalPrice = null;
        }
    }
}