package com.backthree.cohobby.domain.like.service;

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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 이미 좋아요가 있는지 확인
        Optional<Like> existingLike = likeRepository.findByUserAndPost(user, post);
        
        if (existingLike.isPresent()) {
            // 좋아요가 있으면 삭제 (좋아요 취소)
            likeRepository.delete(existingLike.get());
            return false; // 좋아요 취소됨
        } else {
            // 좋아요가 없으면 생성
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
            return true; // 좋아요 추가됨
        }
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return likeRepository.existsByUserAndPost(user, post);
    }

    @Transactional(readOnly = true)
    public List<GetPostResponse> getLikedPosts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<Like> likes = likeRepository.findByUser(user);
        
        // Like 엔티티에서 Post를 추출하고, Image 엔티티 로딩 (LAZY 로딩을 위해)
        List<Post> posts = likes.stream()
                .map(Like::getPost)
                .collect(Collectors.toList());
        
        // Image 엔티티 로딩 (LAZY 로딩을 위해)
        posts.forEach(post -> post.getImages().size());
        
        return posts.stream()
                .map(GetPostResponse::fromEntity)
                .collect(Collectors.toList());
    }
}

