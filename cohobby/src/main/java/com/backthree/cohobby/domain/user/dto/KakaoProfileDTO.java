package com.backthree.cohobby.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoProfileDTO {
    //이메일은 바로 가져옴
    @JsonProperty("email")
    private String email;

    //nickname, profileImage는 profile 객체 안에 중첩되어있어서 따로 꺼내줘야 함
    private String nickname;
    private String profileImageUrl;

    //Jackson이 JSON의 'profile' 키를 발견하면 이 메소드를 호출
    // profile 객체(Map) 내부에서 닉네임&이미지 추출하여 DTO 필드에 직접 할당
    @SuppressWarnings("unchecked")
    @JsonProperty("profile")
    private void unpackNestedProfile(Map<String, Object> profile){
        if(profile != null){
            this.nickname = (String)profile.get("nickname");
            this.profileImageUrl = (String)profile.get("profile_image_url");
        }
    }
}
