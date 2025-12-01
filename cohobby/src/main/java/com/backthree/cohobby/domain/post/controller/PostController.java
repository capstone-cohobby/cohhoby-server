package com.backthree.cohobby.domain.post.controller;

import com.backthree.cohobby.domain.post.dto.request.*;
import com.backthree.cohobby.domain.post.dto.response.*;
import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.post.service.AIEstimateService;
import com.backthree.cohobby.domain.post.service.PostQueryService;
import com.backthree.cohobby.domain.post.service.PostService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name="Post", description = "게시물 관련 API")
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostQueryService postQueryService;
    private final AIEstimateService aiService;
    @Operation(summary = "게시글 초안 생성", description = "물품 정보 입력을 시작할 때 게시글의 초기 DRAFT 상태를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode="201", description="게시물 생성 성공"),
    })
    @ErrorDocs({ErrorStatus.USER_NOT_FOUND})
    @PostMapping()
    public BaseResponse<CreatePostResponse> createPost(
            @Valid @RequestBody CreatePostRequest request,
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        CreatePostResponse payload = postService.createPost(request, user.getId());
        return BaseResponse.onSuccess(SuccessStatus._CREATED, payload);

    }

    @Operation(summary = "게시물 기본 상세 정보 수정", description = "대여 게시물 업로드 과정에서 구입 일시, 하자 사항, 대여 가능 기간 입력 받기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입력된 게시물 기본 상세 정보 수정 완료"),
    })
    @ErrorDocs({ ErrorStatus.POST_NOT_FOUND, ErrorStatus.POST_AUTHOR_MISMATCH, ErrorStatus.POST_STATUS_CONFLICT })
    @PatchMapping("/{postId}/details")
    public BaseResponse<UpdateDetailResponse> updateDetailPost(
            @PathVariable Long postId,
            @Valid @RequestBody UpdateDetailRequest request,
            @Parameter(hidden = true) @CurrentUser User user
    ){
        UpdateDetailResponse payload = postService.updateDetailPost(postId, request, user.getId());
        return BaseResponse.onSuccess(SuccessStatus._OK, payload);

    }

    @Operation(summary = "게시물 가격 필드 정보 수정 & PUBLISHED 전환", description = "대여 게시물 업로드 과정에서 일일 대여료, 보증금를 저장하고 PUBLISHED로 전환합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입력된 게시물 가격 정보 수정 완료")
    })
    @ErrorDocs({ ErrorStatus.POST_NOT_FOUND, ErrorStatus.POST_AUTHOR_MISMATCH, ErrorStatus.POST_STATUS_CONFLICT })
    @PatchMapping("/{postId}/pricing")
    public BaseResponse<UpdatePricingResponse> updatePricingPost(
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePricingRequest request,
            @Parameter(hidden = true) @CurrentUser User user
    ){
        UpdatePricingResponse payload = postService.updatePricingPost(postId, request, user.getId());
        return BaseResponse.onSuccess(SuccessStatus._OK, payload);

    }

    // 게시물 이미지 업로드
    @PostMapping(value = "/{postId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "게시물 이미지 업로드", description = "물품 등록 과정 중 - 물품 사진을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 이미지 추가 완료")
    })
    @ErrorDocs({ErrorStatus.POST_NOT_FOUND, ErrorStatus.POST_AUTHOR_MISMATCH, ErrorStatus.POST_STATUS_CONFLICT})
    public BaseResponse<UpdateImageResponse> updateImagePost(
            @PathVariable Long postId,
            @Valid @ModelAttribute UpdateImageRequest request,
            @CurrentUser User user
    ){
        UpdateImageResponse response = postService.updateS3Images(postId, request, user.getId());
        return BaseResponse.onSuccess(SuccessStatus._OK, response);

    }

    @Operation(summary = "ai 호출", description = "ai 호출해서 추천 대여가, 보증금, 대여 규칙, reason, confidence 응답받기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ai api 응답 완료"),
    })
    @PostMapping("/{postId}/ai-estimate")
    public BaseResponse<AiEstimateResponse> estimate (
            @PathVariable Long postId,
            @Valid @RequestBody AiEstimateClientRequest request,
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        AiEstimateResponse payload = aiService.aiEstimate(request,postId, user.getId());
        return BaseResponse.onSuccess(SuccessStatus._OK, payload);
    }

    @Operation(summary = "게시물 조회(검색)", description = "검색어로 게시물을 조회합니다. 상품명에 검색어가 포함된 게시물을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 조회 성공"),
    })
    @GetMapping("/search")
    public BaseResponse<List<GetPostResponse>> getPostsBySearch(
            @Parameter(description = "검색어", example = "나이키")
            @RequestParam(required = false) String query
    ) {
        List<Post> posts = postQueryService.getPosts(query, "search", null);
        // Image 엔티티 로딩 (LAZY 로딩을 위해)
        posts.forEach(post -> post.getImages().size());
        List<GetPostResponse> response = posts.stream()
                .map(GetPostResponse::fromEntity)
                .collect(Collectors.toList());
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Operation(summary = "게시물 조회(카테고리)", description = "카테고리 ID로 게시물을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 조회 성공"),
    })
    @GetMapping("/category/{categoryId}")
    public BaseResponse<List<GetPostResponse>> getPostsByCategory(
            @Parameter(description = "카테고리 ID", example = "1")
            @PathVariable Long categoryId
    ) {
        List<Post> posts = postQueryService.getPosts(String.valueOf(categoryId), "category", null);
        // Image 엔티티 로딩 (LAZY 로딩을 위해)
        posts.forEach(post -> post.getImages().size());
        List<GetPostResponse> response = posts.stream()
                .map(GetPostResponse::fromEntity)
                .collect(Collectors.toList());
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Operation(summary = "게시물 조회(취미)", description = "취미 ID로 게시물을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 조회 성공"),
    })
    @GetMapping("/hobby/{hobbyId}")
    public BaseResponse<List<GetPostResponse>> getPostsByHobby(
            @Parameter(description = "취미 ID", example = "1")
            @PathVariable Long hobbyId
    ) {
        List<Post> posts = postQueryService.getPosts(String.valueOf(hobbyId), "hobby", null);
        // Image 엔티티 로딩 (LAZY 로딩을 위해)
        posts.forEach(post -> post.getImages().size());
        List<GetPostResponse> response = posts.stream()
                .map(GetPostResponse::fromEntity)
                .collect(Collectors.toList());
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Operation(summary = "게시물 상세 조회", description = "게시물 ID로 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 상세 조회 성공"),
    })
    @ErrorDocs({ErrorStatus.POST_NOT_FOUND})
    @GetMapping("/{postId}")
    public BaseResponse<GetPostDetailResponse> getPostDetail(
            @Parameter(description = "게시물 ID", example = "1")
            @PathVariable Long postId
    ) {
        GetPostDetailResponse response = postService.getPostDetail(postId);
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Operation(summary = "AI 리포트 조회", description = "대여 게시글에서 리포트 조회 시")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리포트 조회 성공")
    })
    @ErrorDocs({ErrorStatus.REPORT_NOT_FOUND})
    @GetMapping("/ai-estimate/{postId}")
    public BaseResponse<AiEstimateResponse>getEstimate(@PathVariable Long postId){
        AiEstimateResponse response = aiService.getEstimateByPostId(postId);
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }
    /**
     * 현재 로그인한 사용자 ID를 가져옵니다. 로그인하지 않은 경우 null을 반환합니다.
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return ((User) authentication.getPrincipal()).getId();
        }
        return null;
    }
    @Operation(summary = "내 등록 상품 조회", description = "현재 사용자가 등록한 게시물 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 상품 조회 성공"),
    })
    @GetMapping("/my-posts")
    public BaseResponse<List<GetPostResponse>> getMyPosts(
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        List<GetPostResponse> response = postService.getMyPosts(user.getId());
        return BaseResponse.onSuccess(SuccessStatus._OK, response);
    }

}
