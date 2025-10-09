package com.backthree.cohobby.domain.chatting.repository;

import com.backthree.cohobby.domain.chatting.entity.Chatting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChattingRepository extends JpaRepository<Chatting, Long> {
    List<Chatting> findByRoomId(Long roomId);
    long countByRoom_IdAndIdGreaterThanAndSender_IdNot(Long roomId, Long id, Long senderId);
}