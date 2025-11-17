package com.backthree.cohobby.domain.hobby.controller;

import com.backthree.cohobby.domain.hobby.dto.response.GetHobbyResponse;
import com.backthree.cohobby.domain.hobby.service.HobbyService;
import com.backthree.cohobby.global.common.BaseResponse;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import com.backthree.cohobby.global.config.swagger.ErrorDocs;
import com.fasterxml.jackson.databind.ser.Serializers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="Hobby", description="취미 관련 API")
@RestController
@RequestMapping("/hobbys")
public class HobbyController {
    private final HobbyService hobbyService;

    @Operation(summary="취미 id 리스트 호출", description = "게시물 등록 화면 렌더링 시 id를 미리 가져옴")
    @ApiResponse({
            @ApiResponse(responseCode = "200", description = "취미 id 호출 성공")
    })
    @ErrorDocs({ErrorStatus.USER_NOT_FOUND})
    @GetMapping()
    public BaseResponse<List<GetHobbyResponse>> getHobbbies(){
        List<GetHobbyResponse> hobbies = hobbyService.getHobbies();
        return BaseResponse.onSuccess(hobbies);
    }
}
