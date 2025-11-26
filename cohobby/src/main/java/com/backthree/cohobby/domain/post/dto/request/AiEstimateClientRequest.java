package com.backthree.cohobby.domain.post.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Getter
@Setter
public class AiEstimateClientRequest {
    @Schema(description = "상품명", example="루이지노 사브레 픽시 자전거")
    @NotBlank(message = "상품명은 필수 입력 값입니다")
    @JsonProperty("name")
    private String goods;

    // 핵심 변경: String hobbyName -> Long hobbyId
    @Schema(description = "취미 ID", example = "1")
    @NotNull(message = "취미 ID는 필수 입력 값입니다")
    private Long hobbyId;

    @Schema(description = "구입 시기", example = "2023-05-22")
    private LocalDate purchaseAt;

    @Schema(description = "상태", example = "스크래치와 사용감이 있습니다\n" +
            "체인 교체 했습니다 새거로 두번째 사진에 있습니다 브레이크 고쳐야합니다\n" +
            "크랙 없고 기스만 좀 있습니다")
    private String defectStatus;
}
