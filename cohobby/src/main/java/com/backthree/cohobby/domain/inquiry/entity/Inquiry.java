package com.backthree.cohobby.domain.inquiry.entity;

import com.backthree.cohobby.domain.common.BaseTimeEntity;
import com.backthree.cohobby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inquiry extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 127) private String title;
    @Lob private String content;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private InquiryStatus status;

    @Enumerated(EnumType.STRING)
    private InquiryType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}