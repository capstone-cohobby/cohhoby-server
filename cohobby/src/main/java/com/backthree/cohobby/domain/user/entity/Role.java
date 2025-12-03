package com.backthree.cohobby.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    USER("USER"),      // 일반 사용자
    ADMIN("ADMIN");    // 관리자

    private final String value;

    @JsonCreator
    public static Role from(String s) {
        if (s == null) {
            return USER; // 기본값
        }
        try {
            return Role.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return USER; // 기본값
        }
    }
}

