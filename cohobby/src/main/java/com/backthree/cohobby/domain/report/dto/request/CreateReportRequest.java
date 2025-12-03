package com.backthree.cohobby.domain.report.dto.request;

import com.backthree.cohobby.domain.report.entity.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

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

    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "연체일수 (반납 연체 신고 시 필수)", example = "3")
    private Integer delayDays;
}

