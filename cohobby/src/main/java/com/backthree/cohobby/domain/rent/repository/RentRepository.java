package com.backthree.cohobby.domain.rent.repository;

import com.backthree.cohobby.domain.post.entity.Post;
import com.backthree.cohobby.domain.rent.entity.Rent;
import com.backthree.cohobby.domain.rent.entity.RentStatus;
import com.backthree.cohobby.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
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

    // 대여 종료일이 오늘인 대여 조회 (보증금 자동결제용)
    @Query("SELECT r FROM Rent r WHERE DATE(r.duedate) = :date AND r.status = :status AND r.deposit > 0")
    List<Rent> findByDuedateDateAndStatus(@Param("date") LocalDate date, @Param("status") RentStatus status);

    // 대여 시작일이 오늘인 대여 조회 (대여료 자동결제용)
    @Query("SELECT r FROM Rent r WHERE DATE(r.startAt) = :date AND r.status = :status AND r.totalPrice > 0")
    List<Rent> findByStartAtDateAndStatus(@Param("date") LocalDate date, @Param("status") RentStatus status);

    boolean existsByBorrowerAndStatusIn(User borrower, Collection<RentStatus> statuses);
    
    // Post에 대한 활성 대여(CONFIRMED, ONGOING) 존재 여부 확인
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Rent r WHERE r.post = :post AND r.status IN :statuses")
    boolean existsByPostAndStatusIn(@Param("post") Post post, @Param("statuses") Collection<RentStatus> statuses);
}
