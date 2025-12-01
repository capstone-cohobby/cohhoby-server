package com.backthree.cohobby.domain.like.controller;

import com.backthree.cohobby.domain.like.service.LikeService;
import com.backthree.cohobby.domain.post.dto.response.GetPostResponse;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.global.annotation.CurrentUser;
import com.backthree.cohobby.global.common.BaseResponse;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import com.backthree.cohobby.global.common.response.status.SuccessStatus;
import com.backthree.cohobby.global.config.swagger.ErrorDocs;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="Like", description = "좋아요 관련 API")
@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "좋아요 토글", description = "게시물에 좋아요를 추가하거나 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 토글 성공"),
    })
    @ErrorDocs({ErrorStatus.POST_NOT_FOUND, ErrorStatus.USER_NOT_FOUND})
    @PostMapping("/{postId}")
    public BaseResponse<LikeToggleResponse> toggleLike(
            @Parameter(description = "게시물 ID", example = "1")
            @PathVariable Long postId,
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        boolean isLiked = likeService.toggleLike(postId, user.getId());
        LikeToggleResponse response = new LikeToggleResponse(isLiked);
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Operation(summary = "좋아요 여부 확인", description = "현재 사용자가 해당 게시물에 좋아요를 눌렀는지 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 여부 확인 성공"),
    })
    @ErrorDocs({ErrorStatus.POST_NOT_FOUND, ErrorStatus.USER_NOT_FOUND})
    @GetMapping("/{postId}")
    public BaseResponse<LikeStatusResponse> checkLikeStatus(
            @Parameter(description = "게시물 ID", example = "1")
            @PathVariable Long postId,
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        boolean isLiked = likeService.isLiked(postId, user.getId());
        LikeStatusResponse response = new LikeStatusResponse(isLiked);
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Operation(summary = "좋아요한 게시물 목록 조회", description = "현재 사용자가 좋아요한 게시물 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요한 게시물 목록 조회 성공"),
    })
    @ErrorDocs({ErrorStatus.USER_NOT_FOUND})
    @GetMapping("/my")
    public BaseResponse<List<GetPostResponse>> getLikedPosts(
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        List<GetPostResponse> likedPosts = likeService.getLikedPosts(user.getId());
        return BaseResponse.onSuccess(SuccessStatus._OK, likedPosts);
    }

    // Response DTOs
    public static class LikeToggleResponse {
        @JsonProperty("isLiked")
        private boolean isLiked;

        public LikeToggleResponse(boolean isLiked) {
            this.isLiked = isLiked;
        }

        @JsonProperty("isLiked")
        public boolean isLiked() {
            return isLiked;
        }
    }

    public static class LikeStatusResponse {
        @JsonProperty("isLiked")
        private boolean isLiked;

        public LikeStatusResponse(boolean isLiked) {
            this.isLiked = isLiked;
        }

        @JsonProperty("isLiked")
        public boolean isLiked() {
            return isLiked;
        }
    }
}

