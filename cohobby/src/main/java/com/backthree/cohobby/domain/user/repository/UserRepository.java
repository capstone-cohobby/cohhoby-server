package com.backthree.cohobby.domain.user.repository;

import com.backthree.cohobby.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //이미 가입된 사용자인지 이메일로 판단
    Optional<User> findByEmail(String email);
    Optional<User> findByProviderId(String providerId);
}