package com.backthree.cohobby.domain.category.repository;

import com.backthree.cohobby.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}