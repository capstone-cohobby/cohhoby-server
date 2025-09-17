package com.backthree.cohobby.domain.review.entity;

import com.backthree.cohobby.domain.common.BaseTimeEntity;
import com.backthree.cohobby.domain.rent.entity.Rent;
import com.backthree.cohobby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Short rate;          // SMALLINT

    @Column(columnDefinition = "json")
    private String tags;         // JSON → String

    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rentId", nullable = false)
    private Rent rent;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="reviewerId", nullable = false)
    private User reviewer;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="revieweeId", nullable = false)
    private User reviewee;
}