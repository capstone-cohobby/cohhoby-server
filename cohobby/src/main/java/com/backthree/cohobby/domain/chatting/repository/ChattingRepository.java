package com.backthree.cohobby.domain.chatting.repository;

import com.backthree.cohobby.domain.chatting.entity.Chatting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChattingRepository extends JpaRepository<Chatting, Long> {
}