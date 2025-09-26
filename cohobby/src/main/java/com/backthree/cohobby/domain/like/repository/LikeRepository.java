package com.backthree.cohobby.domain.like.repository;

import com.backthree.cohobby.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
}