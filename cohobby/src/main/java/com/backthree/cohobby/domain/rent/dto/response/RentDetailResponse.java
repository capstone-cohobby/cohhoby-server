package com.backthree.cohobby.domain.rent.dto.response;

import com.backthree.cohobby.domain.rent.entity.Rent;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RentDetailResponse {
    private Long id;
    private LocalDateTime startAt;
    private LocalDateTime duedate;
    private String rule;
    private String status;
    private Integer totalPrice;
    private String currency;
    private Long postId;
    private String postGoods;

    public static RentDetailResponse fromEntity(Rent rent) {
        return RentDetailResponse.builder()
                .id(rent.getId())
                .startAt(rent.getStartAt())
                .duedate(rent.getDuedate())
                .rule(rent.getRule())
                .status(rent.getStatus() != null ? rent.getStatus().name() : null)
                .totalPrice(rent.getTotalPrice())
                .currency(rent.getCurrency())
                .postId(rent.getPost() != null ? rent.getPost().getId() : null)
                .postGoods(rent.getPost() != null ? rent.getPost().getGoods() : null)
                .build();
    }
}

