package com.backthree.cohobby.domain.rent.repository;

import com.backthree.cohobby.domain.rent.entity.Rent;
import com.backthree.cohobby.domain.rent.entity.RentStatus;
import com.backthree.cohobby.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RentRepository extends JpaRepository<Rent, Long> {

    // ChattingRoom에서 Rent를 조인해 조회
    @Query("select c.rent from ChattingRoom c where c.id = :roomId")
    Optional<Rent> findByChattingRoomId(@Param("roomId") Long roomId);

    // borrower로 대여 내역 조회
    List<Rent> findByBorrowerOrderByCreatedAtDesc(User borrower);

    // borrower와 status로 대여 내역 조회
    List<Rent> findByBorrowerAndStatus(User borrower, RentStatus status);

    // borrower가 나이고 CREATED가 아닌 대여 내역 조회
    List<Rent> findByBorrowerAndStatusNot(User borrower, RentStatus status);
}
