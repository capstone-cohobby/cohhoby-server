package com.backthree.cohobby.domain.post.dto.response;

import com.backthree.cohobby.domain.post.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class GetPostResponse {
    @Schema(description = "게시물 ID", example = "123")
    private Long postId;

    @Schema(description = "상품명", example = "나이키 에어포스")
    private String goods;

    @Schema(description = "일일 대여료", example = "5000")
    private Integer dailyPrice;

    @Schema(description = "보증금", example = "50000")
    private Integer deposit;

    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "대여 가능 시작일", example = "2024-01-01")
    private LocalDate availableFrom;

    @Schema(description = "대여 가능 종료일", example = "2024-12-31")
    private LocalDate availableUntil;

    @Schema(description = "취미 이름", example = "운동")
    private String hobbyName;

    @Schema(description = "카테고리 이름", example = "스포츠")
    private String categoryName;

    @Schema(description = "작성자 ID", example = "1")
    private Long userId;

    @Schema(description = "작성자 닉네임", example = "user123")
    private String userNickname;

    public static GetPostResponse fromEntity(Post post) {
        // 이미지 URL 추출: Post.imageUrl(대표 이미지)를 우선 사용, 없으면 Image 엔티티의 첫 번째 이미지 사용
        String imageUrl = post.getImageUrl();
        
        // Post.imageUrl이 없으면 Image 엔티티에서 첫 번째 이미지 사용
        if (imageUrl == null && post.getImages() != null && !post.getImages().isEmpty()) {
            imageUrl = post.getImages().stream()
                    .filter(image -> image.getImageUrl() != null)
                    .sorted((img1, img2) -> Long.compare(
                        img1.getId() != null ? img1.getId() : Long.MAX_VALUE,
                        img2.getId() != null ? img2.getId() : Long.MAX_VALUE
                    )) // ID 순서로 정렬 (가장 먼저 등록한 이미지)
                    .map(image -> image.getImageUrl())
                    .findFirst()
                    .orElse(null);
        }

        return GetPostResponse.builder()
                .postId(post.getId())
                .goods(post.getGoods())
                .dailyPrice(post.getDailyPrice())
                .deposit(post.getDeposit())
                .imageUrl(imageUrl)
                .availableFrom(post.getAvailableFrom())
                .availableUntil(post.getAvailableUntil())
                .hobbyName(post.getHobby() != null ? post.getHobby().getName() : null)
                .categoryName(post.getHobby() != null && post.getHobby().getCategory() != null 
                        ? post.getHobby().getCategory().getName() : null)
                .userId(post.getUser() != null ? post.getUser().getId() : null)
                .userNickname(post.getUser() != null ? post.getUser().getNickname() : null)
                .build();
    }
}

