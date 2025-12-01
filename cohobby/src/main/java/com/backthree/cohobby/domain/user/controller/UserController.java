package com.backthree.cohobby.domain.user.controller;

import com.backthree.cohobby.domain.like.service.LikeService;
import com.backthree.cohobby.domain.post.dto.response.GetPostResponse;
import com.backthree.cohobby.domain.user.dto.UserResponseDTO;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.service.UserService;
import com.backthree.cohobby.global.annotation.CurrentUser;
import com.backthree.cohobby.global.common.BaseResponse;
import com.backthree.cohobby.global.common.response.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="User", description = "사용자 관련 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final LikeService likeService;

    @Operation(summary = "사용자 프로필 조회", description = "사용자 ID로 프로필 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 프로필 조회 성공"),
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(UserResponseDTO.from(userService.findUserById(userId)));
    }

    @Operation(summary = "내가 찜한 게시글 목록 조회", description = "현재 사용자가 찜한 게시물 목록을 조회합니다. 카테고리 ID를 선택적으로 제공하면 해당 카테고리별로 필터링됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "찜한 게시물 조회 성공"),
    })
    @GetMapping("/my-likes")
    public BaseResponse<List<GetPostResponse>> getMyLikes(
            @Parameter(description = "카테고리 ID (선택사항)", example = "1")
            @RequestParam(required = false) Long categoryId,
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        List<GetPostResponse> response = likeService.getMyLikes(user.getId(), categoryId);
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }
}

