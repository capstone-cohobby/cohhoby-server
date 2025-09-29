package com.backthree.cohobby.domain.post.controller;

import com.backthree.cohobby.domain.post.dto.request.CreatePostRequest;
import com.backthree.cohobby.domain.post.dto.response.CreatePostResponse;
import com.backthree.cohobby.domain.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="Post", description = "게시물 관련 API")
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @Operation(summary = "게시글 초안 생성", description = "물품 정보 입력을 시작할 때 게시글의 초기 DRAFT 상태를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode="201", description="게시물 생성 성공"),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 유저/취미 ID")
    })
    @PostMapping()
    public ResponseEntity<CreatePostResponse> createPost(
            @Valid @RequestBody CreatePostRequest request   // JSON 본문을 받기 위해 @RequestBody 사용, 유효성 검사를 위해 @Valid 추가
    ) {
        CreatePostResponse response = postService.createPost(request);

        // HTTP 상태 201 Created 와 함께 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
