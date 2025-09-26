package com.backthree.cohobby.domain.rent.repository;

import com.backthree.cohobby.domain.rent.entity.Rent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentRepository extends JpaRepository<Rent, Long> {
}