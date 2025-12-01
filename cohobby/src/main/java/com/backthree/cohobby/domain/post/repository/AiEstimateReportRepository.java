package com.backthree.cohobby.domain.post.repository;

import com.backthree.cohobby.domain.post.entity.AiEstimateReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiEstimateReportRepository extends JpaRepository<AiEstimateReport,Long>{
    Optional<AiEstimateReport> findByPostId(Long postId);
}
