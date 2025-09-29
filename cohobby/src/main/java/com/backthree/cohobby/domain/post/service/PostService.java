package com.backthree.cohobby.domain.post.service;

import com.backthree.cohobby.domain.hobby.entity.Hobby;
import com.backthree.cohobby.domain.hobby.repository.HobbyRepository;
import com.backthree.cohobby.domain.post.dto.request.CreatePostRequest;
import com.backthree.cohobby.domain.post.dto.response.CreatePostResponse;
import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.post.repository.PostRepository;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final HobbyRepository hobbyRepository;

    private static final Long DUMMY_USER_ID = 1L;  //임시 유저

    @Transactional
    public CreatePostResponse createPost(CreatePostRequest request){
        // ID를 기반으로 각 엔티티가 존재하는지 조회
        // 더미 유저 조회 (없으면 예외)
        User user = userRepository.findById(DUMMY_USER_ID)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND, "id=" + request.getUserId()));
        Hobby hobby = hobbyRepository.findById(request.getHobbyId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.HOBBY_NOT_FOUND,"id=" + request.getHobbyId()));

        // Post 엔티티 생성
        Post newPost = Post.builder()
                .goods(request.getGoods())
                .user(user)
                .hobby(hobby)
                .build();

        // 생성된 Post 엔티티를 Repository에 저장
        Post savedPost = postRepository.save(newPost);

        return CreatePostResponse.fromEntity(savedPost);
    }
}
