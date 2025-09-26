package com.backthree.cohobby.domain.payment.repository;

import com.backthree.cohobby.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}