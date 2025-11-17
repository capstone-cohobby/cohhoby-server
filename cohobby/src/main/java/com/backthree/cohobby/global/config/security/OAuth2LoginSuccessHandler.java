package com.backthree.cohobby.global.config.security;

import com.backthree.cohobby.domain.user.dto.TokenDTO;
import com.backthree.cohobby.domain.user.entity.RefreshToken;
import com.backthree.cohobby.domain.user.repository.RefreshTokenRepository;
import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException{
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        //JWT 생성
        TokenDTO tokenDTO = jwtService.createTokenDTO(email);

        //DB에 리프레시 토큰 저장 또는 업데이트
        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        rt -> rt.updateToken(tokenDTO.getRefreshToken()), //이미 있으면 토큰 값만 갱신
                        () -> refreshTokenRepository.save( //없으면 토큰 새로 생성
                                RefreshToken.builder()
                                        .user(user)
                                        .token(tokenDTO.getRefreshToken())
                                        .build())
                );

        //JWT(tokenDTO의 토큰들) 담아서 메인페이지로 리다이렉트
        String targetUrl;
        targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000")
                .queryParam("accessToken", tokenDTO.getAccessToken())
                .queryParam("refreshToken", tokenDTO.getRefreshToken())
                .build().toUriString();

        response.sendRedirect(targetUrl);
    }

    private boolean isTestProfile() {
        String profile = System.getProperty("spring.profiles.active");
        return "testapp".equals(profile);
    }
}
