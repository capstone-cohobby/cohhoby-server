package com.backthree.cohobby.domain.report.entity;

import com.backthree.cohobby.domain.common.BaseTimeEntity;
import com.backthree.cohobby.domain.rent.entity.Rent;
import com.backthree.cohobby.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 127) private String title;
    @Lob private String content;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ReportStatus status; // ENUM → String
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ReportType type;   // ENUM → String

    @Column(name = "image_url", length = 127)
    private String imageUrl;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "rentId", nullable = false)
    private Rent rent;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    // 반납 연체일수 (반납 연체 신고 시 사용)
    private Integer delayDays;

    // 상태 업데이트 메서드
    public void updateStatus(ReportStatus status) {
        this.status = status;
    }
}