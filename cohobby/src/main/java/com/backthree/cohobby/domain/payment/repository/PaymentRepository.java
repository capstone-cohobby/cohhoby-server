package com.backthree.cohobby.domain.payment.repository;

import com.backthree.cohobby.domain.payment.entity.Payment;
import com.backthree.cohobby.domain.rent.entity.Rent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPgOrderNo(String pgOrderNo);
    List<Payment> findByRent(Rent rent);
}