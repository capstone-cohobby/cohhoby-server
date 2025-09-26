package com.backthree.cohobby.domain.hobby.entity;

import com.backthree.cohobby.domain.category.entity.Category;
import com.backthree.cohobby.domain.common.BaseTimeEntity;
import com.backthree.cohobby.domain.contribution.entity.Contribution;
import com.backthree.cohobby.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hobby extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 31)
    private String name;

    @Column(nullable = false)
    private Integer score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "hobby")
    private Set<Contribution> contributions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "hobby")
    private Set<Post> posts = new LinkedHashSet<>();

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    public void setContributions(Set<Contribution> contributions) {
        this.contributions = contributions;
    }
}