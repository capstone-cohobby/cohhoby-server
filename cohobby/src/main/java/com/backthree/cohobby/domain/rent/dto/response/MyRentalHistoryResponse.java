package com.backthree.cohobby.domain.rent.dto.response;

import com.backthree.cohobby.domain.rent.entity.Rent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class MyRentalHistoryResponse {
    @Schema(description = "대여 ID", example = "567")
    private Long id;

    @Schema(description = "게시물 ID")
    private Long postId;

    @Schema(description = "게시물 상품명")
    private String postGoods;

    @Schema(description = "게시물 이미지 URL")
    private String postImageUrl;

    @Schema(description = "빌려준 사람 닉네임")
    private String ownerNickname;

    @Schema(description = "대여 시작일")
    private LocalDate startAt;

    @Schema(description = "대여 종료일")
    private LocalDate duedate;

    @Schema(description = "총 대여료")
    private Integer totalPrice;

    @Schema(description = "대여 상태")
    private String status;

    public static MyRentalHistoryResponse fromEntity(Rent rent) {
        LocalDate startAtDate = rent.getStartAt() != null ? rent.getStartAt().toLocalDate() : null;
        LocalDate duedateDate = rent.getDuedate() != null ? rent.getDuedate().toLocalDate() : null;
        
        // 게시물 이미지 URL 가져오기 (첫 번째 이미지)
        String imageUrl = null;
        if (rent.getPost() != null) {
            if (rent.getPost().getImages() != null && !rent.getPost().getImages().isEmpty()) {
                imageUrl = rent.getPost().getImages().iterator().next().getImageUrl();
            } else if (rent.getPost().getImageUrl() != null) {
                imageUrl = rent.getPost().getImageUrl();
            }
        }
        
        return MyRentalHistoryResponse.builder()
                .id(rent.getId())
                .postId(rent.getPost() != null ? rent.getPost().getId() : null)
                .postGoods(rent.getPost() != null ? rent.getPost().getGoods() : null)
                .postImageUrl(imageUrl)
                .ownerNickname(rent.getOwner() != null ? rent.getOwner().getNickname() : null)
                .startAt(startAtDate)
                .duedate(duedateDate)
                .totalPrice(rent.getTotalPrice())
                .status(rent.getStatus() != null ? rent.getStatus().name() : null)
                .build();
    }
}

