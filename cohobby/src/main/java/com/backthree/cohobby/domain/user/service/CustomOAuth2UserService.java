package com.backthree.cohobby.domain.user.service;

import com.backthree.cohobby.domain.user.dto.KakaoUserInfo;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest){
        //카카오 인증된 유저 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        var attributes = oAuth2User.getAttributes();
// [ ‼️ 디버그 코드 추가 ‼️ ]
        System.out.println("==========================================");
        System.out.println("### KAKAO ATTRIBUTES ###: " + attributes);
        System.out.println("==========================================");
        //우리 서비스에 등록되어있는 유저인지 이메일로 검증
        KakaoUserInfo userInfo = new KakaoUserInfo(attributes);
        String email = userInfo.getProfileDTO().getEmail(); //이메일로 기존 회원 조회
        String providerId = userInfo.getProviderId(); //providerId는 신규 가입 시에만 사용
        if (email == null) {
            throw new OAuth2AuthenticationException("카카오로부터 계정의 이메일 주소를 받지 못했습니다.");
        }
        //등록 안되어있는 유저이면 db에 새로 저장
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                                    .providerId(providerId)
                                    .nickname(userInfo.getProfileDTO().getNickname())
                                    .email(email)
                                    .profilePicture(userInfo.getProfileDTO().getProfileImageUrl())
                                    .score(0)
                                    .isBanned(false)
                                    .build();
                    return userRepository.save(newUser);
                });

        // 5. 'authentication.getName()'이 email를 반환하도록 속성 맵을 설정
        //Spring security가 email 속성을 찾을 수 있또록 attributes에 추가
        Map<String, Object> customAttributes = new HashMap<>(attributes);
        customAttributes.put("email", email);

        // 6. oAuth2User 원본 대신, 우리 'User' 정보가 담긴 새 'DefaultOAuth2User'를 반환
        return new DefaultOAuth2User(
                user.getAuthorities(), // User 엔티티에 정의된 getAuthorities() 호출 (ROLE_USER 반환)
                customAttributes,      // 'email' 키가 추가된 속성 맵
                "email"
        );
    }


}
