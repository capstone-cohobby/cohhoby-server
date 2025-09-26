package com.backthree.cohobby.domain.post.entity;

import com.backthree.cohobby.domain.common.BaseTimeEntity;
import com.backthree.cohobby.domain.hobby.entity.Hobby;
import com.backthree.cohobby.domain.rent.entity.Rent;
import com.backthree.cohobby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 127)
    private String title;

    @Column(length = 500)
    private String description;

    @Column private Integer price;
    @Column private Integer deposit;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hobbyId")
    private Hobby hobby;

    @OneToMany(mappedBy = "post")
    private Set<Image> images = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "likes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post")
    private Set<Rent> rents = new LinkedHashSet<>();

    public void setRents(Set<Rent> rents) {
        this.rents = rents;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }
}