package com.backthree.cohobby.domain.hobby.repository;

import com.backthree.cohobby.domain.hobby.entity.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HobbyRepository extends JpaRepository<Hobby, Long> {
}