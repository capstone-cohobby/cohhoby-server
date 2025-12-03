package com.backthree.cohobby.domain.report.dto.response;

import com.backthree.cohobby.domain.report.entity.Report;
import com.backthree.cohobby.domain.report.entity.ReportStatus;
import com.backthree.cohobby.domain.report.entity.ReportType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReportResponse {
    private Long id;
    private Long rentId;
    private Long userId;
    private String title;
    private String content;
    private ReportType type;
    private ReportStatus status;
    private String imageUrl;
    private Integer delayDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReportResponse from(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .rentId(report.getRent().getId())
                .userId(report.getUser().getId())
                .title(report.getTitle())
                .content(report.getContent())
                .type(report.getType())
                .status(report.getStatus())
                .imageUrl(report.getImageUrl())
                .delayDays(report.getDelayDays())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}

