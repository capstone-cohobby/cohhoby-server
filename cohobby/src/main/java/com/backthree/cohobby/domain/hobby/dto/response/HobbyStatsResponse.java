package com.backthree.cohobby.domain.hobby.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HobbyStatsResponse {
    @Schema(description = "총 경험치", example = "5000")
    private Integer totalExperience;

    @Schema(description = "대여한 물품 개수", example = "10")
    private Integer totalRentedItems;

    @Schema(description = "기여한 취미 개수", example = "5")
    private Integer contributedHobbiesCount;

    @Schema(description = "취미별 정보 목록")
    private List<HobbyInfo> hobbies;

    @Getter
    @Builder
    public static class HobbyInfo {
        @Schema(description = "취미 ID", example = "1")
        private Long hobbyId;

        @Schema(description = "취미 이름", example = "골프")
        private String name;

        @Schema(description = "카테고리 이름", example = "스포츠")
        private String categoryName;

        @Schema(description = "취미 점수", example = "100000")
        private Integer score;

        @Schema(description = "진척도 (0-100)", example = "10.0")
        private Double progress;

        @Schema(description = "대여 완료 여부", example = "true")
        private Boolean contributed;
    }
}

