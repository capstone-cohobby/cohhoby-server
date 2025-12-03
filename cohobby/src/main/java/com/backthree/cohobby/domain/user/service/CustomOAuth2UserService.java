package com.backthree.cohobby.domain.user.service;

import com.backthree.cohobby.domain.user.dto.KakaoUserInfo;
import com.backthree.cohobby.domain.user.entity.Role;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest){
        try {
            //카카오 인증된 유저 가져오기
            OAuth2User oAuth2User = super.loadUser(userRequest);
            var attributes = oAuth2User.getAttributes();
            
            //우리 서비스에 등록되어있는 유저인지 이메일로 검증
            KakaoUserInfo userInfo = new KakaoUserInfo(attributes);
            String email = userInfo.getProfileDTO().getEmail(); //이메일로 기존 회원 조회
            String providerId = userInfo.getProviderId(); //providerId는 신규 가입 시에만 사용
            
            log.debug("카카오 로그인 - 이메일: {}, Provider ID: {}", email, providerId);
            
            if (email == null || email.isEmpty()) {
                log.error("⚠️ 이메일이 null입니다. 카카오 개발자 콘솔에서 이메일 수집 동의를 활성화했는지 확인하세요.");
                OAuth2Error oauth2Error = new OAuth2Error(
                    "email_not_provided",
                    "카카오로부터 계정의 이메일 주소를 받지 못했습니다. 카카오 개발자 콘솔에서 이메일 수집 동의를 활성화해주세요.",
                    null
                );
                throw new OAuth2AuthenticationException(oauth2Error);
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
                                        .role(Role.USER)  // 기본값은 USER
                                        .birthYear(null)  // 초기 가입 시에는 생년월일 정보 없음 (나중에 추가 정보 입력)
                                        .birthday(null)   // 초기 가입 시에는 생년월일 정보 없음
                                        .gender(null)     // 초기 가입 시에는 성별 정보 없음
                                        .phoneNumber(null) // 초기 가입 시에는 전화번호 정보 없음
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
        } catch (OAuth2AuthenticationException e) {
            log.error("OAuth2 인증 예외 발생: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
            OAuth2Error oauth2Error = new OAuth2Error(
                "oauth2_authentication_error",
                "OAuth2 인증 중 오류가 발생했습니다: " + e.getMessage(),
                null
            );
            throw new OAuth2AuthenticationException(oauth2Error, e);
        }
    }


}
