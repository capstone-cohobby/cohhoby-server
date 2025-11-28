package com.backthree.cohobby.domain.rent.controller;

import com.backthree.cohobby.domain.rent.dto.request.UpdateDetailRequest;
import com.backthree.cohobby.domain.rent.dto.response.RentDetailResponse;
import com.backthree.cohobby.domain.rent.dto.response.UpdateDetailResponse;
import com.backthree.cohobby.domain.rent.service.RentService;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rent", description = "대여 관련 API")
@RestController
@RequestMapping("/rents")
@RequiredArgsConstructor
public class RentController {
    private final RentService rentService;

    @Operation(summary = "대여 정보 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대여 정보 조회 성공")
    })
    @ErrorDocs({ErrorStatus.USER_NOT_FOUND})
    @GetMapping("/{roomId}/detail")
    public BaseResponse<RentDetailResponse> getDetail(
            @PathVariable Long roomId,
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        RentDetailResponse payload = rentService.getDetail(roomId, user.getId());
        return BaseResponse.onSuccess(SuccessStatus._OK, payload);
    }

    @Operation(summary = "대여 정보 수정 API", description = "대여 날짜, 대여 규칙, 일일 대여료 중 하나만 body에 넣어야함")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대여 정보 수정 성공")
    })
    @ErrorDocs({ErrorStatus.USER_NOT_FOUND})
    @PatchMapping("/{roomId}/detail")
    public BaseResponse<UpdateDetailResponse> updateDetail(
            @PathVariable Long roomId,
            @Valid @RequestBody UpdateDetailRequest request,
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        UpdateDetailResponse payload = rentService.updateDetail(roomId, request, user.getId());
        return BaseResponse.onSuccess(SuccessStatus._OK, payload);
    }
}
