package com.backthree.cohobby.domain.post.repository;

import com.backthree.cohobby.domain.post.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}