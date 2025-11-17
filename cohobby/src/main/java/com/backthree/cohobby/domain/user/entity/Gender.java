package com.backthree.cohobby.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    MALE("MALE"),
    FEMALE("FEMALE");

    private final String value;
    @JsonCreator
    public static Gender from(String s) {
        // 문자열을 대문자로 바꾸고 일치하는 Enum 값을 찾아 반환
        return Gender.valueOf(s.toUpperCase());
    }
}
