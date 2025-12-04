package com.backthree.cohobby.domain.post.dto.response;

import com.backthree.cohobby.domain.post.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GetPostDetailResponse {
    @Schema(description = "게시물 ID", example = "123")
    private Long postId;

    @Schema(description = "상품명", example = "나이키 에어포스")
    private String goods;

    @Schema(description = "일일 대여료", example = "5000")
    private Integer dailyPrice;

    @Schema(description = "보증금", example = "50000")
    private Integer deposit;

    @Schema(description = "이미지 URL 목록")
    private List<String> images;

    @Schema(description = "대여 가능 시작일", example = "2024-01-01")
    private LocalDate availableFrom;

    @Schema(description = "대여 가능 종료일", example = "2024-12-31")
    private LocalDate availableUntil;

    @Schema(description = "구매일", example = "2023-01-01")
    private LocalDate purchasedAt;

    @Schema(description = "하자 사항", example = "전체적으로 깨끗한 상태입니다.")
    private String defectStatus;

    @Schema(description = "주의사항", example = "렌즈 청소 시 전용 천을 사용해주세요.")
    private String caution;

    @Schema(description = "취미 이름", example = "운동")
    private String hobbyName;

    @Schema(description = "카테고리 이름", example = "스포츠")
    private String categoryName;

    @Schema(description = "작성자 ID", example = "1")
    private Long userId;

    @Schema(description = "작성자 닉네임", example = "user123")
    private String userNickname;

    @Schema(description = "작성자 프로필 사진", example = "https://example.com/profile.jpg")
    private String userProfilePicture;
    
    @Schema(description = "대여 가능 여부", example = "true")
    private Boolean available;

    public static GetPostDetailResponse fromEntity(Post post, boolean available) {
        // 이미지 URL 목록 추출 (ID 순서대로 정렬 - 가장 먼저 등록한 이미지부터)
        List<String> imageUrls = post.getImages().stream()
                .filter(image -> image.getImageUrl() != null)
                .sorted((img1, img2) -> Long.compare(
                    img1.getId() != null ? img1.getId() : Long.MAX_VALUE,
                    img2.getId() != null ? img2.getId() : Long.MAX_VALUE
                )) // ID 순서로 정렬 (가장 먼저 등록한 이미지가 먼저)
                .map(image -> image.getImageUrl())
                .collect(Collectors.toList());
        
        // 이미지가 없고 imageUrl이 있으면 imageUrl 추가
        if (imageUrls.isEmpty() && post.getImageUrl() != null) {
            imageUrls.add(post.getImageUrl());
        }

        return GetPostDetailResponse.builder()
                .postId(post.getId())
                .goods(post.getGoods())
                .dailyPrice(post.getDailyPrice())
                .deposit(post.getDeposit())
                .images(imageUrls)
                .availableFrom(post.getAvailableFrom())
                .availableUntil(post.getAvailableUntil())
                .purchasedAt(post.getPurchasedAt())
                .defectStatus(post.getDefectStatus())
                .caution(post.getCaution())
                .hobbyName(post.getHobby() != null ? post.getHobby().getName() : null)
                .categoryName(post.getHobby() != null && post.getHobby().getCategory() != null 
                        ? post.getHobby().getCategory().getName() : null)
                .userId(post.getUser() != null ? post.getUser().getId() : null)
                .userNickname(post.getUser() != null ? post.getUser().getNickname() : null)
                .userProfilePicture(post.getUser() != null ? post.getUser().getProfilePicture() : null)
                .available(available)
                .build();
    }
}

