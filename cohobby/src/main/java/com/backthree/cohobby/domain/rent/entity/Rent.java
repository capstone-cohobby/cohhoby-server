package com.backthree.cohobby.domain.rent.entity;

import com.backthree.cohobby.domain.common.BaseTimeEntity;
import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
}