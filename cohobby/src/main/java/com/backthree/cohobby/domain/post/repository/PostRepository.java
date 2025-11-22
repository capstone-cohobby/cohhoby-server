package com.backthree.cohobby.domain.post.repository;

import com.backthree.cohobby.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostQueryDsl {
    //id로 채팅방 찾는 메서드
    Post findById(long id);
}