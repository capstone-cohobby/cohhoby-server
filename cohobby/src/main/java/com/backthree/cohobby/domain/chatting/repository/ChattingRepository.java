package com.backthree.cohobby.domain.chatting.repository;

import com.backthree.cohobby.domain.chatting.entity.Chatting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChattingRepository extends JpaRepository<Chatting, Long> {
    List<Chatting> findByRoomId(Long roomId);

    long countByRoom_IdAndIdGreaterThanAndSender_IdNot(Long roomId, Long id, Long senderId);

    @Query("SELECT c FROM Chatting c WHERE c.room.id = :roomId ORDER BY c.time DESC, c.id DESC")
    List<Chatting> findTopByRoomIdOrderByTimeDescIdDesc(@Param("roomId") Long roomId);
    
    default Optional<Chatting> findLatestByRoomId(Long roomId) {
        List<Chatting> results = findTopByRoomIdOrderByTimeDescIdDesc(roomId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}