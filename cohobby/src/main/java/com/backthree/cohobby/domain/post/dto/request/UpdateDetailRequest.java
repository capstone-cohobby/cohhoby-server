package com.backthree.cohobby.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateDetailRequest {
    @Schema(description = "물품에 대한 상세 설명", example = "2024년 10월에 구매한 최신형 모델입니다. 실사용 5회 미만으로 상태 좋습니다.")
    private String defectStatus;

    @Schema(description = "물품 구매일", example = "2024-10-02")
    private LocalDate purchasedAt;

    @Schema(description = "대여 가능 시작일", example = "2025-10-03")
    private LocalDate availableFrom;

    @Schema(description = "대여 가능 종료일", example = "2025-12-31")
    private LocalDate availableUntil;

    @Schema(description = "주의 사항", example = "신발 밑창이 떨어지지 않게 해주세요")
    private String caution;

}
