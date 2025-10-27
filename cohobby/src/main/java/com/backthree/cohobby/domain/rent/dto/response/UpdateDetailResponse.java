package com.backthree.cohobby.domain.rent.dto.response;

import com.backthree.cohobby.domain.rent.entity.Rent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateDetailResponse {
    @Schema(description = "수정된 대여 ID", example = "567")
    private Long rentId;

    public static UpdateDetailResponse fromEntity(Rent rent) {
        return UpdateDetailResponse.builder()
                .rentId(rent.getId())
                .build();
    }
}
