package com.backthree.cohobby.domain.user.dto;

import com.backthree.cohobby.domain.user.entity.Gender;
import com.backthree.cohobby.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Getter
public class KakaoUserInfo {
    private final String providerId; //카카오 provider 아이디
    private final KakaoProfileDTO profileDTO;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.providerId = String.valueOf(attributes.get("id"));
        ObjectMapper mapper = new ObjectMapper();
        this.profileDTO = mapper.convertValue(attributes.get("kakao_account"), KakaoProfileDTO.class);
    }

    public User toEntity(){
        String email = profileDTO.getEmail();
        String nickname = profileDTO.getNickname();
        String profileImage = profileDTO.getProfileImageUrl();

        /* Kakao account dto 사용할 때 검사할 내용
        //형변환
        //birthyear 들어왔으면 integer로 변환
        Integer birthYear = birthYearStr != null ? Integer.valueOf(birthYearStr) : null;
        //birthday 들어왔으면 localdate로 변환
        LocalDate birthday = null;
        // birthyear와 birthday가 모두 존재하고, birthday가 4자리(MMdd)일 때만 파싱 시도
        if (birthYearStr != null && !birthYearStr.isEmpty() && birthdayStr != null && birthdayStr.length() == 4) {
            try {
                // ex. "2003" + "-" + "01" + "-" + "30" -> "2003-01-30"
                String fullBirthDate = birthYearStr + "-" + birthdayStr.substring(0, 2) + "-" + birthdayStr.substring(2, 4);
                birthday = LocalDate.parse(fullBirthDate);
            } catch (DateTimeParseException e) {
                System.err.println("KakaoUserInfo: 생일 파싱 실패. " + e.getMessage());
                // 파싱 실패 시 null 유지
            }
        }*/
        return User.builder()
                .email(email)
                .nickname(nickname)
                .profilePicture(profileImage)
                .providerId(this.providerId)
                .score(0)
                .isBanned(false)
                .build();
    }
}
