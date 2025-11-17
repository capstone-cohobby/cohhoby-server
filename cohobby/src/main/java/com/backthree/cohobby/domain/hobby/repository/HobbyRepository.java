package com.backthree.cohobby.domain.hobby.repository;

import com.backthree.cohobby.domain.hobby.entity.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HobbyRepository extends JpaRepository<Hobby, Long> {
    Optional<Hobby> findByCategoryAndName(String category, String name);

}