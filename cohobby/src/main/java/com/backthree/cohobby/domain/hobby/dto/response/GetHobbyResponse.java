package com.backthree.cohobby.domain.hobby.dto.response;

import com.backthree.cohobby.domain.hobby.entity.Hobby;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetHobbyResponse {
    @Schema(description = "hobbyId", example = "1")
    private Long id;
    @Schema(description = "categoryId", example = "1")
    private Long categoryId;


    public static GetHobbyResponse fromEntity(Hobby hobby) {
        return GetHobbyResponse.builder()
                .id(hobby.getId())
                .categoryId(hobby.getCategory().getId())
                .build();
    }
}
