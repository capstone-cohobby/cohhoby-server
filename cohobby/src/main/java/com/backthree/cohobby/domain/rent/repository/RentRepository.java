package com.backthree.cohobby.domain.rent.repository;

import com.backthree.cohobby.domain.rent.entity.Rent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RentRepository extends JpaRepository<Rent, Long> {

    // ChattingRoom에서 Rent를 조인해 조회
    @Query("select c.rent from ChattingRoom c where c.id = :roomId")
    Optional<Rent> findByChattingRoomId(@Param("roomId") Long roomId);
}
