package com.backthree.cohobby.domain.user.dto;

import com.backthree.cohobby.domain.user.entity.Gender;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true) //dto에 정의되지 않은 항목은 무시하도록 설정
public class KakaoAccountDTO {
    @JsonProperty("email")
    private String email;

    @JsonProperty("birthyear")
    private String birthYear;

    @JsonProperty("birthday")
    private String birthday;

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("profile")
    private KakaoProfileDTO profile;

}
