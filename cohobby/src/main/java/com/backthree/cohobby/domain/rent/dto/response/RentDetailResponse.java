package com.backthree.cohobby.domain.rent.dto.response;

import com.backthree.cohobby.domain.rent.entity.Rent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class RentDetailResponse {
    @Schema(description = "대여 ID", example = "567")
    private Long id;

    @Schema(description = "대여 시작일")
    private LocalDate startAt;

    @Schema(description = "대여 종료일")
    private LocalDate duedate;

    @Schema(description = "대여 규칙")
    private String rule;

    @Schema(description = "대여 상태")
    private String status;

    @Schema(description = "총 대여료")
    private Integer totalPrice;

    @Schema(description = "일일 대여료")
    private Integer dailyPrice;

    @Schema(description = "통화", example = "KRW")
    private String currency;

    @Schema(description = "게시물 ID")
    private Long postId;

    @Schema(description = "게시물 상품명")
    private String postGoods;

    public static RentDetailResponse fromEntity(Rent rent) {
        LocalDate startAtDate = rent.getStartAt() != null ? rent.getStartAt().toLocalDate() : null;
        LocalDate duedateDate = rent.getDuedate() != null ? rent.getDuedate().toLocalDate() : null;
        
        return RentDetailResponse.builder()
                .id(rent.getId())
                .startAt(startAtDate)
                .duedate(duedateDate)
                .rule(rent.getRule())
                .status(rent.getStatus() != null ? rent.getStatus().name() : null)
                .totalPrice(rent.getTotalPrice())
                .dailyPrice(rent.getDailyPrice())
                .currency(rent.getCurrency())
                .postId(rent.getPost() != null ? rent.getPost().getId() : null)
                .postGoods(rent.getPost() != null ? rent.getPost().getGoods() : null)
                .build();
    }
}

