package com.backthree.cohobby.domain.chatting.repository;

import com.backthree.cohobby.domain.chatting.entity.ChattingRoom;
import com.backthree.cohobby.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {
    List<ChattingRoom> findByOwnerOrBorrower(User owner, User borrower);
    Optional<ChattingRoom> findByOwnerAndBorrower(User owner, User borrower);
}
