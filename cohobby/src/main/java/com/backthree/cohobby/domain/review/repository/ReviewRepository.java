package com.backthree.cohobby.domain.review.repository;

import com.backthree.cohobby.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}