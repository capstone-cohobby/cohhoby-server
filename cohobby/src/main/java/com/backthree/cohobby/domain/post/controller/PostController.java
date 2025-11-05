package com.backthree.cohobby.domain.post.controller;

import com.backthree.cohobby.domain.post.dto.request.*;
import com.backthree.cohobby.domain.post.dto.response.*;
import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.post.service.PostQueryService;
import com.backthree.cohobby.domain.post.service.PostService;
import com.backthree.cohobby.global.common.BaseResponse;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import com.backthree.cohobby.global.common.response.status.SuccessStatus;
import com.backthree.cohobby.global.config.swagger.ErrorDocs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="Post", description = "게시물 관련 API")
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostQueryService postQueryService;
    @Operation(summary = "게시글 초안 생성", description = "물품 정보 입력을 시작할 때 게시글의 초기 DRAFT 상태를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode="201", description="게시물 생성 성공"),
    })
    @ErrorDocs({ErrorStatus.USER_NOT_FOUND})
    @PostMapping()
    public BaseResponse<CreatePostResponse> createPost(
            @Valid @RequestBody CreatePostRequest request   // JSON 본문을 받기 위해 @RequestBody 사용, 유효성 검사를 위해 @Valid 추가
    ) {
        CreatePostResponse payload = postService.createPost(request);
        return BaseResponse.onSuccess(SuccessStatus._CREATED, payload);

    }

    @Operation(summary = "게시물 기본 상세 정보 수정", description = "대여 게시물 업로드 과정에서 구입 일시, 하자 사항, 주의 사항, 대여 가능 기간 입력 받기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입력된 게시물 기본 상세 정보 수정 완료"),
    })
    @ErrorDocs({ ErrorStatus.POST_NOT_FOUND, ErrorStatus.POST_AUTHOR_MISMATCH, ErrorStatus.POST_STATUS_CONFLICT })
    @PatchMapping("/{postId}/details")
    public BaseResponse<UpdateDetailResponse> updateDetailPost(
            @PathVariable Long postId,
            @Valid @RequestBody UpdateDetailRequest request
    ){
        UpdateDetailResponse payload = postService.updateDetailPost(postId,request);
        return BaseResponse.onSuccess(SuccessStatus._OK, payload);

    }

    @Operation(summary = "게시물 가격 필드 정보 수정", description = "대여 게시물 업로드 과정에서 일일/주간 대여료, 보증금 추천 받고 설정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입력된 게시물 가격 정보 수정 완료")
    })
    @ErrorDocs({ ErrorStatus.POST_NOT_FOUND, ErrorStatus.POST_AUTHOR_MISMATCH, ErrorStatus.POST_STATUS_CONFLICT })
    @PatchMapping("/{postId}/pricing")
    public BaseResponse<UpdatePricingResponse> updatePricingPost(
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePricingRequest request
    ){
        UpdatePricingResponse payload = postService.updatePricingPost(postId,request);
        return BaseResponse.onSuccess(SuccessStatus._OK, payload);

    }

    @GetMapping("")
    public List<Post> getPosts(
            @RequestParam String query,
            @RequestParam String type
    ){
        List<Post> result = postQueryService.getPosts(query, type);
        return result;
    }


}
