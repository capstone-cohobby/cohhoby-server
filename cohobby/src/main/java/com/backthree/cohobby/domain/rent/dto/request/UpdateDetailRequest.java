package com.backthree.cohobby.domain.rent.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateDetailRequest {
    @Schema(description = "대여 시작 날짜", example = "2024-10-02")
    private LocalDate startAt;

    @Schema(description = "대여 종료 날짜", example = "2025-10-03")
    private LocalDate duedate;

    @Schema(description = "대여 규칙", example = "신발 밑창이 떨어질시 : 5만원")
    private String rule;

    @Schema(description = "일일 대여료", example = "8000")
    private Integer dailyPrice;
}
