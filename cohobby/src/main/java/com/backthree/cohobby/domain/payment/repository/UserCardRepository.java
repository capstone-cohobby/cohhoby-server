package com.backthree.cohobby.domain.payment.repository;

import com.backthree.cohobby.domain.payment.entity.UserCard;
import com.backthree.cohobby.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCardRepository extends JpaRepository<UserCard, Long> {
    Optional<UserCard> findByUser(User user);
    Optional<UserCard> findByUserId(Long userId);
}

