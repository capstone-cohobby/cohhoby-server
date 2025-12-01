package com.backthree.cohobby.domain.like.service;

import com.backthree.cohobby.domain.like.dto.response.CreateLikeResponse;
import com.backthree.cohobby.domain.like.dto.response.DeleteLikeResponse;
import com.backthree.cohobby.domain.like.entity.Like;
import com.backthree.cohobby.domain.like.repository.LikeRepository;
import com.backthree.cohobby.domain.post.dto.response.GetPostResponse;
import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.post.repository.PostRepository;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.UserRepository;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import com.backthree.cohobby.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateLikeResponse createLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        
        // 이미 찜한 게시물인지 확인
        if (likeRepository.existsByUserAndPost(user, post)) {
            throw new GeneralException(ErrorStatus.LIKE_ALREADY_EXISTS);
        }
        
        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        
        Like savedLike = likeRepository.save(like);
        
        return CreateLikeResponse.from(savedLike.getId(), postId);
    }

    @Transactional
    public DeleteLikeResponse deleteLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        
        Like like = likeRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new GeneralException(ErrorStatus.LIKE_NOT_FOUND));
        
        likeRepository.delete(like);
        
        return DeleteLikeResponse.from(postId);
    }

    @Transactional(readOnly = true)
    public List<GetPostResponse> getMyLikes(Long userId, Long categoryId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        
        // 전체 찜 목록 조회
        List<Like> likes = likeRepository.findByUserOrderByCreatedAtDesc(user);
        
        // Image 엔티티 로딩 (LAZY 로딩을 위해)
        likes.forEach(like -> {
            if (like.getPost() != null) {
                Post post = like.getPost();
                // Post의 Hobby와 Category 로딩
                if (post.getHobby() != null) {
                    post.getHobby().getCategory();
                }
                if (post.getImages() != null) {
                    post.getImages().size();
                }
            }
        });
        
        // 카테고리 필터링 (서비스 레이어에서 처리)
        return likes.stream()
                .map(Like::getPost)
                .filter(post -> post != null)
                .filter(post -> {
                    // categoryId가 null이면 모든 게시물 반환
                    if (categoryId == null) {
                        return true;
                    }
                    // categoryId가 있으면 해당 카테고리만 필터링
                    return post.getHobby() != null 
                            && post.getHobby().getCategory() != null
                            && post.getHobby().getCategory().getId().equals(categoryId);
                })
                .map(GetPostResponse::fromEntity)
                .collect(Collectors.toList());
    }
}

