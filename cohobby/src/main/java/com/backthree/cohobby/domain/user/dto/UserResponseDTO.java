package com.backthree.cohobby.domain.user.dto;

import com.backthree.cohobby.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponseDTO {
    private Long id;
    private String nickname;
    private String email;
    private String profilePicture;
    private Integer score;
    private String gender;
    private Integer birthYear;
    private LocalDate birthday;
    private String phoneNumber;
    private LocalDateTime createdAt;

    public static UserResponseDTO from(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .score(user.getScore())
                .gender(user.getGender() != null ? user.getGender().name() : null)
                .birthYear(user.getBirthYear())
                .birthday(user.getBirthday())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

