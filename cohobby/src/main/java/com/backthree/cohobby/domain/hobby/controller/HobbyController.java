package com.backthree.cohobby.domain.hobby.controller;

import com.backthree.cohobby.domain.hobby.dto.response.HobbyStatsResponse;
import com.backthree.cohobby.domain.hobby.service.HobbyService;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.global.annotation.CurrentUser;
import com.backthree.cohobby.global.common.BaseResponse;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import com.backthree.cohobby.global.common.response.status.SuccessStatus;
import com.backthree.cohobby.global.config.swagger.ErrorDocs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name="Hobby", description="취미 관련 API")
@RestController
@RequestMapping("/hobbies")
@RequiredArgsConstructor
public class HobbyController {
    private final HobbyService hobbyService;

    @Operation(summary = "나의 취미 통계 조회", description = "현재 사용자의 취미 통계 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취미 통계 조회 성공"),
    })
    @ErrorDocs({ErrorStatus.USER_NOT_FOUND})
    @GetMapping("/my-stats")
    public BaseResponse<HobbyStatsResponse> getMyHobbyStats(
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        HobbyStatsResponse stats = hobbyService.getMyHobbyStats(user.getId());
        return BaseResponse.onSuccess(SuccessStatus._OK, stats);
    }
}