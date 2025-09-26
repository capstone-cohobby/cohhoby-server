package com.backthree.cohobby.domain.refund.repository;

import com.backthree.cohobby.domain.refund.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {
}