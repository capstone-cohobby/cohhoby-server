package com.backthree.cohobby.domain.user.repository;

import com.backthree.cohobby.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}