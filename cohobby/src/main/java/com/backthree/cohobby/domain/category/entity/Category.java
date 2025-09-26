package com.backthree.cohobby.domain.category.entity;

import com.backthree.cohobby.domain.hobby.entity.Hobby;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 31)
    private String name;

    @OneToMany(mappedBy = "category")
    private Set<Hobby> hobbies = new LinkedHashSet<>();

}

