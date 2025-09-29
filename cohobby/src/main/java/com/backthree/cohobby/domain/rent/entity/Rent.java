package com.backthree.cohobby.domain.rent.entity;

import com.backthree.cohobby.domain.common.BaseTimeEntity;
import com.backthree.cohobby.domain.payment.entity.Payment;
import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.rent.alert.entity.RentAlert;
import com.backthree.cohobby.domain.report.entity.Report;
import com.backthree.cohobby.domain.review.entity.Review;
import com.backthree.cohobby.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
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

    @Column(length = 3)
    private String currency;

    // JSON 미정 → String + columnDefinition
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

}