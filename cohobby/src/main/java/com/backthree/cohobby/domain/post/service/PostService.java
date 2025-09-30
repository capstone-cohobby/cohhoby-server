package com.backthree.cohobby.domain.post.service;

import com.backthree.cohobby.domain.hobby.entity.Hobby;
import com.backthree.cohobby.domain.post.dto.request.CreatePostRequest;
import com.backthree.cohobby.domain.post.dto.response.CreatePostResponse;
import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.post.repository.PostRepository;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.service.UserService;
import com.backthree.cohobby.domain.hobby.service.HobbyService;
import com.backthree.cohobby.global.exception.GeneralException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final HobbyService hobbyService;

    private static final Long DUMMY_USER_ID = 1L;  //임시 유저

    @Transactional
    public CreatePostResponse createPost(CreatePostRequest request){

        Long hobbyId = request.getHobbyId();

        // ID를 기반으로 각 엔티티가 존재하는지 조회
        // 더미 유저 조회 (없으면 예외)
        if (!userService.existsById(DUMMY_USER_ID)) {
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }
        if (!hobbyService.existsById(hobbyId)) {
            throw new GeneralException(ErrorStatus.HOBBY_NOT_FOUND);
        }

        // 검증 후, 참조(프록시)만 가져와서 외래키 관계 설정

        User userReference = userService.findUserReferenceById(DUMMY_USER_ID);
        Hobby hobbyReference = hobbyService.findHobbyReferenceById(hobbyId);

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
}
