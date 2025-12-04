package com.backthree.cohobby.domain.report.controller;

import com.backthree.cohobby.domain.report.dto.request.CreateReportRequest;
import com.backthree.cohobby.domain.report.dto.response.ReportResponse;
import com.backthree.cohobby.domain.report.service.ReportService;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.global.annotation.CurrentUser;
import com.backthree.cohobby.global.common.BaseResponse;
import com.backthree.cohobby.global.common.response.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Report", description = "신고 관련 API")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "신고 생성", description = "대여 관련 신고를 생성합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<ReportResponse>> createReport(
            @Valid @ModelAttribute CreateReportRequest request,
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        ReportResponse response = reportService.createReport(request, user);
        return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus._CREATED, response));
    }

    @Operation(summary = "신고 조회", description = "본인이 작성한 신고를 조회합니다.")
    @GetMapping("/{reportId}")
    public ResponseEntity<BaseResponse<ReportResponse>> getReport(
            @PathVariable Long reportId,
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        ReportResponse response = reportService.getReport(reportId, user);
        return ResponseEntity.ok(BaseResponse.onSuccess(response));
    }

    @Operation(summary = "내 신고 목록 조회", description = "본인이 작성한 모든 신고 목록을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<BaseResponse<List<ReportResponse>>> getMyReports(
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        List<ReportResponse> response = reportService.getMyReports(user);
        return ResponseEntity.ok(BaseResponse.onSuccess(response));
    }
}

