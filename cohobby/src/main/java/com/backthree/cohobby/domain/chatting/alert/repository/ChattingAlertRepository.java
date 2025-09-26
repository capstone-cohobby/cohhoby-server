package com.backthree.cohobby.domain.chatting.alert.repository;

import com.backthree.cohobby.domain.chatting.alert.entity.ChattingAlert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChattingAlertRepository extends JpaRepository<ChattingAlert, Integer> {
}