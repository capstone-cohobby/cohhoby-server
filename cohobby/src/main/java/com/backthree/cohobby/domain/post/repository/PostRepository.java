package com.backthree.cohobby.domain.post.repository;

import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostQueryDsl {
    //id로 채팅방 찾는 메서드
    Post findById(long id);
    
    // 사용자가 등록한 게시물 조회
    List<Post> findByUserOrderByCreatedAtDesc(User user);
}