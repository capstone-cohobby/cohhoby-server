package com.backthree.cohobby.domain.report.dto.request;

import com.backthree.cohobby.domain.report.entity.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class CreateReportRequest {
    @Schema(description = "대여 ID", example = "1")
    @NotNull(message = "대여 ID는 필수 입력 값입니다.")
    private Long rentId;

    @Schema(description = "신고 유형", example = "DAMAGE")
    @NotNull(message = "신고 유형은 필수 선택 값입니다.")
    private ReportType type;

    @Schema(description = "신고 제목", example = "물품 파손 신고")
    @NotBlank(message = "신고 제목은 필수 입력 값입니다.")
    private String title;

    @Schema(description = "신고 내용", example = "물품이 심하게 파손되었습니다.")
    @NotBlank(message = "신고 내용은 필수 입력 값입니다.")
    private String content;

    @Schema(description = "이미지 파일 목록 (S3 업로드, 최대 5개)")
    @Size(max = 5, message = "이미지는 최대 5개까지 업로드할 수 있습니다.")
    private List<MultipartFile> images;

    @Schema(description = "연체일수 (반납 연체 신고 시 필수)", example = "3")
    private Integer delayDays;
}

