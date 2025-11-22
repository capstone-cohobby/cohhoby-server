package com.backthree.cohobby.domain.post.service;

import com.backthree.cohobby.domain.hobby.entity.Hobby;
import com.backthree.cohobby.domain.post.dto.request.*;
import com.backthree.cohobby.domain.post.dto.response.*;
import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.post.entity.PostStatus;
import com.backthree.cohobby.domain.post.repository.PostRepository;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.service.UserService;
import com.backthree.cohobby.domain.hobby.service.HobbyService;
import com.backthree.cohobby.global.exception.GeneralException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final HobbyService hobbyService;

    @Transactional
    public CreatePostResponse createPost(CreatePostRequest request, Long userId){
        Long hobbyId = request.getHobbyId();

        // ID를 기반으로 각 엔티티가 존재하는지 조회
        // 더미 유저 조회 (없으면 예외)
        if (!userService.existsById(userId)) {
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }
        if (!hobbyService.existsById(hobbyId)) {
            throw new GeneralException(ErrorStatus.HOBBY_NOT_FOUND);
        }

        // 검증 후, 참조(프록시)만 가져와서 외래키 관계 설정
        User userReference = userService.findUserReferenceById(userId);
        Hobby hobbyReference = hobbyService.findHobbyById(hobbyId);

        // Post 엔티티 생성
        Post newPost = Post.builder()
                .goods(request.getGoods())
                .user(userReference)
                .hobby(hobbyReference)
                .build();

        // 생성된 Post 엔티티를 Repository에 저장
        Post savedPost = postRepository.save(newPost);

        return CreatePostResponse.fromEntity(savedPost);
    }

    @Transactional
    public UpdateDetailResponse updateDetailPost(Long postId, UpdateDetailRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        // 권한 및 상태 가드
        if (!Objects.equals(post.getUser().getId(), userId)) {
            throw new GeneralException(ErrorStatus.POST_AUTHOR_MISMATCH);
        }
        if (post.getStatus() != PostStatus.DRAFT) {
            throw new GeneralException(ErrorStatus.POST_STATUS_CONFLICT);
        }

        // 부분 업데이트 (null 무시)
        if (request.getPurchasedAt() != null)      post.setPurchasedAt(request.getPurchasedAt());
        if (request.getAvailableFrom() != null)    post.setAvailableFrom(request.getAvailableFrom());
        if (request.getAvailableUntil() != null)   post.setAvailableUntil(request.getAvailableUntil());
        if (request.getDefectStatus() != null)     post.setDefectStatus(request.getDefectStatus());

        // 도메인 불변식(기간/구입일 등) 검증 - 엔티티 메서드가 있다면 그걸 호출
        post.validatePeriod();

        postRepository.save(post);
        return UpdateDetailResponse.fromEntity(post);
    }
    @Transactional
    public UpdatePricingResponse updatePricingPost(Long postId, UpdatePricingRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        // 권한 및 상태 가드
        if (!Objects.equals(post.getUser().getId(), userId)) {
            throw new GeneralException(ErrorStatus.POST_AUTHOR_MISMATCH);
        }
        if (post.getStatus() != PostStatus.DRAFT) {
            throw new GeneralException(ErrorStatus.POST_STATUS_CONFLICT);
        }
        // 부분 업데이트 (null 무시)
        if (request.getDailyPrice() != null)      post.setDailyPrice(request.getDailyPrice());
        if (request.getDeposit() != null)      post.setDeposit(request.getDeposit());
        if(request.getCaution() != null)       post.setCaution(request.getCaution());
        post.setStatus(PostStatus.PUBLISHED);
        postRepository.save(post);

        return UpdatePricingResponse.fromEntity(post);
    }

}
