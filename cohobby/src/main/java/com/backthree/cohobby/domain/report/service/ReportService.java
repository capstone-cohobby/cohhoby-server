package com.backthree.cohobby.domain.report.service;

import com.backthree.cohobby.domain.payment.service.PaymentService;
import com.backthree.cohobby.domain.rent.entity.Rent;
import com.backthree.cohobby.domain.rent.repository.RentRepository;
import com.backthree.cohobby.domain.report.dto.request.CreateReportRequest;
import com.backthree.cohobby.domain.report.dto.response.ReportResponse;
import com.backthree.cohobby.domain.report.entity.Report;
import com.backthree.cohobby.domain.report.entity.ReportStatus;
import com.backthree.cohobby.domain.report.entity.ReportType;
import com.backthree.cohobby.domain.report.repository.ReportRepository;
import com.backthree.cohobby.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReportService {
    private final ReportRepository reportRepository;
    private final RentRepository rentRepository;
    private final PaymentService paymentService;

    // 신고 생성
    public ReportResponse createReport(CreateReportRequest request, User user) {
        Rent rent = rentRepository.findById(request.getRentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대여입니다."));

        // 권한 검증: 보증금 자동결제 관련 신고는 owner만 가능
        if (request.getType().requiresAutoPayment()) {
            if (!rent.getOwner().getId().equals(user.getId())) {
                throw new IllegalArgumentException("보증금 자동결제 관련 신고는 빌려준 사람만 가능합니다.");
            }
        } else {
            // 양방향 신고는 owner 또는 borrower만 가능
            if (!rent.getOwner().getId().equals(user.getId()) && 
                !rent.getBorrower().getId().equals(user.getId())) {
                throw new IllegalArgumentException("해당 대여의 당사자만 신고할 수 있습니다.");
            }
        }

        // 반납 연체 신고 시 연체일수 필수
        if (request.getType() == ReportType.RETURN_DELAY) {
            if (request.getDelayDays() == null || request.getDelayDays() <= 0) {
                throw new IllegalArgumentException("반납 연체 신고 시 연체일수는 필수입니다.");
            }
        }

        Report report = Report.builder()
                .rent(rent)
                .user(user)
                .type(request.getType())
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .delayDays(request.getDelayDays())
                .status(ReportStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();

        report = reportRepository.save(report);
        log.info("신고 생성 완료: reportId={}, type={}, userId={}", report.getId(), report.getType(), user.getId());

        return ReportResponse.from(report);
    }

    // 신고 조회 (본인이 작성한 신고만)
    @Transactional(readOnly = true)
    public ReportResponse getReport(Long reportId, User user) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고입니다."));

        // 본인이 작성한 신고만 조회 가능
        if (!report.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인이 작성한 신고만 조회할 수 있습니다.");
        }

        return ReportResponse.from(report);
    }

    // 본인이 작성한 신고 목록 조회
    @Transactional(readOnly = true)
    public List<ReportResponse> getMyReports(User user) {
        return reportRepository.findByUser(user).stream()
                .map(ReportResponse::from)
                .collect(Collectors.toList());
    }

    // 관리자: 신고 승인/거부
    public ReportResponse approveReport(Long reportId, boolean approved) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고입니다."));

        if (report.getStatus() != ReportStatus.OPEN && report.getStatus() != ReportStatus.IN_PROGRESS) {
            throw new IllegalStateException("이미 처리된 신고입니다.");
        }

        if (approved) {
            report.updateStatus(ReportStatus.APPROVED);
            
            // 보증금 자동결제가 필요한 신고인 경우 자동결제 실행
            if (report.getType().requiresAutoPayment()) {
                try {
                    paymentService.processReportAutoPayment(report);
                    log.info("신고 승인 및 자동결제 완료: reportId={}, type={}", report.getId(), report.getType());
                } catch (Exception e) {
                    log.error("신고 승인 후 자동결제 실패: reportId={}, error={}", report.getId(), e.getMessage(), e);
                    // 자동결제 실패해도 신고는 승인 상태로 유지
                }
            }
        } else {
            report.updateStatus(ReportStatus.REJECTED);
        }

        report = reportRepository.save(report);
        return ReportResponse.from(report);
    }

    // 관리자: 모든 신고 목록 조회
    @Transactional(readOnly = true)
    public List<ReportResponse> getAllReports() {
        return reportRepository.findAll().stream()
                .map(ReportResponse::from)
                .collect(Collectors.toList());
    }

    // 관리자: 특정 상태의 신고 목록 조회
    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByStatus(ReportStatus status) {
        return reportRepository.findByStatus(status).stream()
                .map(ReportResponse::from)
                .collect(Collectors.toList());
    }
}

