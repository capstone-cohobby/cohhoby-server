package com.backthree.cohobby.domain.user.service;

import com.backthree.cohobby.domain.user.dto.TokenDTO;
import com.backthree.cohobby.domain.user.dto.request.TestLoginRequest;
import com.backthree.cohobby.domain.user.dto.request.TestSignupRequest;
import com.backthree.cohobby.domain.user.entity.RefreshToken;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.RefreshTokenRepository;
import com.backthree.cohobby.domain.user.repository.UserRepository;
import com.backthree.cohobby.global.config.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TestAuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    // 테스트용 회원가입
    public TokenDTO signup(TestSignupRequest request) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + request.email());
        }

        // providerId 생성 (테스트용: "test_" + email)
        String providerId = "test_" + request.email();

        // providerId 중복 확인
        if (userRepository.findByProviderId(providerId).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        // 사용자 생성
        User user = User.builder()
                .email(request.email())
                .nickname(request.nickname())
                .providerId(providerId)
                .score(0)
                .isBanned(false)
                .build();

        userRepository.save(user);

        // JWT 토큰 생성
        TokenDTO tokenDTO = jwtService.createTokenDTO(request.email());

        // 리프레시 토큰 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(tokenDTO.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);

        log.info("테스트 회원가입 완료: email={}, nickname={}", request.email(), request.nickname());
        return tokenDTO;
    }

    // 테스트용 로그인
    public TokenDTO login(TestLoginRequest request) {
        // 사용자 조회
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다: " + request.email()));

        // 테스트 사용자인지 확인 (providerId가 "test_"로 시작하는지)
        if (!user.getProviderId().startsWith("test_")) {
            throw new IllegalArgumentException("테스트 사용자가 아닙니다. OAuth2 로그인을 사용해주세요.");
        }

        // 계정이 비활성화되었는지 확인
        if (user.getIsBanned()) {
            throw new IllegalArgumentException("비활성화된 계정입니다.");
        }

        // JWT 토큰 생성
        TokenDTO tokenDTO = jwtService.createTokenDTO(request.email());

        // 리프레시 토큰 업데이트 또는 생성
        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        rt -> rt.updateToken(tokenDTO.getRefreshToken()),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .user(user)
                                        .token(tokenDTO.getRefreshToken())
                                        .build()
                        )
                );

        log.info("테스트 로그인 완료: email={}", request.email());
        return tokenDTO;
    }
}

