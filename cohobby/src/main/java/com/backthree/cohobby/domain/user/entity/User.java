package com.backthree.cohobby.domain.user.entity;

import com.backthree.cohobby.domain.common.BaseTimeEntity;
import com.backthree.cohobby.domain.rent.entity.Rent;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTimeEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, length = 63)
    private String nickname;

    @Column(nullable = true, length = 127)
    private String email;

    @Column(length = 127)
    private String profilePicture;

    @Column(nullable = false)
    private Integer score;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = true)
    private Integer birthYear;

    @Column(nullable = true)
    private LocalDate birthday;

    @Column(nullable = false)
    private Boolean isBanned;

    //@Column(nullable = false, length = 31)
    private String phoneNumber;

    //카카오 고유 ID 저장 필드, 카카오 연결 끊기를 위해 필요
    @Column(nullable = false, unique = true)
    private String providerId;

    @OneToMany(mappedBy = "owner")
    private List<Rent> rentsOwned = new ArrayList<>();

    @OneToMany(mappedBy = "borrower")
    private List<Rent> rentsBorrowed = new ArrayList<>();

    public void updateAdditionalInfo(Gender gender, Integer birthYear, LocalDate birthday, String phoneNumber) {
        this.gender = gender;
        this.birthYear = birthYear;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
    }

    //userdetails 인터페이스 구현 메소드 오버라이드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //모든 사용자를 "role_user" 권한으로 통일
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        //OAuth2를 사용하므로 비번을 따로 필요 X
        return null;
    }

    @Override
    public String getUsername() {
        return this.email; //이메일을 식별자로 사용
    }

    //계정 만료
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //계정 잠겼는지
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //자격증명(비번) 만료되었는지
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //계정 활성화 여부 - isBanned 필드 사용
    @Override
    public boolean isEnabled() {
        return !this.isBanned;
    }

    //계정 비활성화
    public void withdraw(){
        this.isBanned = true;
    }
}