package com.backthree.cohobby.domain.post.repository;

import com.backthree.cohobby.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}