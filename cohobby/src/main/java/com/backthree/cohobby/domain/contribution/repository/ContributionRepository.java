package com.backthree.cohobby.domain.contribution.repository;

import com.backthree.cohobby.domain.contribution.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {
}