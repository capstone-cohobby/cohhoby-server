package com.backthree.cohobby.domain.hobby.dto.response;

import com.backthree.cohobby.domain.hobby.entity.Hobby;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetHobbyResponse {
    @Schema(description = "hobbyId", example = "1")
    private Long id;
    @Schema(description = "categoryId", example = "1")
    private String category;


    public static GetHobbyResponse from(Hobby hobby) {
        return new GetHobbyResponse(
                hobby.getId(),
                hobby.getCategory()
        );
    }
}
