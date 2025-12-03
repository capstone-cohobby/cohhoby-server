package com.backthree.cohobby.domain.user.service;

import com.backthree.cohobby.domain.user.dto.TokenDTO;
import com.backthree.cohobby.domain.user.dto.request.AdminLoginRequest;
import com.backthree.cohobby.domain.user.entity.RefreshToken;
import com.backthree.cohobby.domain.user.entity.Role;
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
public class AdminAuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    // 관리자 로그인
    public TokenDTO adminLogin(AdminLoginRequest request) {
        // 사용자 조회
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다: " + request.email()));

        // 관리자 권한 확인
        if (user.getRole() == null || user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("관리자 권한이 없습니다.");
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

        log.info("관리자 로그인 완료: email={}", request.email());
        return tokenDTO;
    }
}

