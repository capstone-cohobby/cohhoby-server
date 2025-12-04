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
import com.backthree.cohobby.global.exception.GeneralException;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReportService {
    private final ReportRepository reportRepository;
    private final RentRepository rentRepository;
    private final PaymentService paymentService;
    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

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

        // 이미지 업로드 (S3) - 최대 5개
        List<String> imageUrls = new ArrayList<>();
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            if (request.getImages().size() > 5) {
                throw new IllegalArgumentException("이미지는 최대 5개까지 업로드할 수 있습니다.");
            }
            for (MultipartFile image : request.getImages()) {
                if (image != null && !image.isEmpty()) {
                    String imageUrl = uploadImageToS3(image);
                    imageUrls.add(imageUrl);
                }
            }
        }

        Report report = Report.builder()
                .rent(rent)
                .user(user)
                .type(request.getType())
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrls(imageUrls)
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

    // S3에 이미지 업로드
    private String uploadImageToS3(MultipartFile file) {
        // 파일 형식 검사
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new GeneralException(ErrorStatus.NOT_IMAGE_FILE);
        }

        try (InputStream inputStream = file.getInputStream()) {
            // 파일 이름 중복 방지(UUID 사용)
            String originalFileName = file.getOriginalFilename();
            String extension = getExtension(originalFileName);
            String uuidFileName = "reports/" + UUID.randomUUID().toString() + extension;

            // 메타데이터 설정
            ObjectMetadata metadata = ObjectMetadata.builder()
                    .contentType(contentType)
                    .build();

            // S3에 업로드
            S3Resource resource = s3Template.upload(bucket, uuidFileName, inputStream, metadata);
            return resource.getURL().toString();
        } catch (IOException e) {
            throw new GeneralException(ErrorStatus.S3_UPLOAD_FAIL);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 확장자 추출 메서드
    private String getExtension(String fileName) {
        if (fileName == null) {
            throw new GeneralException(ErrorStatus.INVALID_FILE_EXTENSION);
        }

        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new GeneralException(ErrorStatus.INVALID_FILE_EXTENSION);
        }
    }
}

