package com.backthree.cohobby.domain.report.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApproveReportRequest {
    @Schema(description = "승인 여부", example = "true")
    private Boolean approved;

    @Schema(description = "관리자 메모", example = "신고 내용 확인 후 승인합니다.")
    private String adminMemo;
}

