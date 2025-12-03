package com.backthree.cohobby.domain.report.controller;

import com.backthree.cohobby.domain.report.dto.response.ReportResponse;
import com.backthree.cohobby.domain.report.entity.ReportStatus;
import com.backthree.cohobby.domain.report.service.ReportService;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.global.annotation.CurrentUser;
import com.backthree.cohobby.global.annotation.RequireAdmin;
import com.backthree.cohobby.global.common.BaseResponse;
import com.backthree.cohobby.global.common.response.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Report", description = "관리자 신고 관리 API")
@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {
    private final ReportService reportService;

    @Operation(summary = "신고 승인/거부", description = "관리자가 신고를 승인하거나 거부합니다. 승인 시 자동결제가 실행됩니다.")
    @RequireAdmin
    @PostMapping("/{reportId}/approve")
    public ResponseEntity<BaseResponse<ReportResponse>> approveReport(
            @PathVariable Long reportId,
            @RequestParam boolean approved,
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        ReportResponse response = reportService.approveReport(reportId, approved);
        return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus._OK, response));
    }

    @Operation(summary = "모든 신고 목록 조회", description = "관리자가 모든 신고 목록을 조회합니다.")
    @RequireAdmin
    @GetMapping
    public ResponseEntity<BaseResponse<List<ReportResponse>>> getAllReports(
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        List<ReportResponse> response = reportService.getAllReports();
        return ResponseEntity.ok(BaseResponse.onSuccess(response));
    }

    @Operation(summary = "상태별 신고 목록 조회", description = "관리자가 특정 상태의 신고 목록을 조회합니다.")
    @RequireAdmin
    @GetMapping("/status/{status}")
    public ResponseEntity<BaseResponse<List<ReportResponse>>> getReportsByStatus(
            @PathVariable ReportStatus status,
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        List<ReportResponse> response = reportService.getReportsByStatus(status);
        return ResponseEntity.ok(BaseResponse.onSuccess(response));
    }
}

