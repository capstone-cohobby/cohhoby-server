package com.backthree.cohobby.domain.user.entity;

import com.backthree.cohobby.domain.common.BaseTimeEntity;
import com.backthree.cohobby.domain.rent.entity.Rent;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 63)
    private String nickname;

    @Column(nullable = false, length = 127)
    private String email;

    @Column(length = 127)
    private String profilePicture;

    @Column(nullable = false)
    private Integer score;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private Integer birthYear;

    @Column(nullable = false)
    private LocalDateTime birthday;

    @Column(nullable = false)
    private Boolean isBanned;

    @Column(nullable = false, length = 31)
    private String phoneNumber;

    @OneToMany(mappedBy = "owner")
    private List<Rent> rentsOwned = new ArrayList<>();

    @OneToMany(mappedBy = "borrower")
    private List<Rent> rentsBorrowed = new ArrayList<>();
}