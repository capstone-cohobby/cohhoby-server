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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.frontend.url}")
    private String frontendUrl; // yml에서 값 주입
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
        // 요청이 어디서 왔는지 확인하여 적절한 곳으로 리다이렉트
        String targetUrl;
        
        // 현재 요청의 호스트와 포트를 동적으로 가져오기
        String scheme = request.getScheme(); // http 또는 https
        String serverName = request.getServerName(); // 호스트명 또는 IP
        int serverPort = request.getServerPort(); // 포트 번호
        
        // 포트가 기본 포트(80, 443)가 아니면 포트 번호 포함
        String baseUrl = (serverPort == 80 || serverPort == 443) 
            ? scheme + "://" + serverName 
            : scheme + "://" + serverName + ":" + serverPort;
        
        // Referer 헤더나 쿼리 파라미터를 확인하여 리다이렉트 대상 결정
        String referer = request.getHeader("Referer");
        String redirectTo = request.getParameter("redirect_to");
        
        // redirect_to 파라미터가 있으면 그곳으로, 없으면 Referer 확인
        if (redirectTo != null && !redirectTo.isEmpty()) {
            targetUrl = UriComponentsBuilder.fromUriString(redirectTo)
                    .queryParam("accessToken", tokenDTO.getAccessToken())
                    .queryParam("refreshToken", tokenDTO.getRefreshToken())
                    .build().toUriString();
        } else if (referer != null && (referer.contains("swagger-ui") || referer.contains("/docs/"))) {
            // Swagger에서 온 경우 Swagger로 리다이렉트 (동적 URL 사용)
            String swaggerPath = referer.contains("/docs/") 
                ? "/docs/swagger-ui/index.html" 
                : "/swagger-ui/index.html";
            targetUrl = UriComponentsBuilder.fromUriString(baseUrl + swaggerPath)
                    .queryParam("accessToken", tokenDTO.getAccessToken())
                    .queryParam("refreshToken", tokenDTO.getRefreshToken())
                    .build().toUriString();
        } else {
            // 기본값: 프론트엔드 로그인 페이지로 리다이렉트 (동적 URL 사용)
            // 서버 포트를 프론트엔드 포트로 변경

            // ... (중략) ...

            // 리다이렉트 로직
            targetUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                    .path("/login")
                    .queryParam("accessToken", tokenDTO.getAccessToken())
                    .queryParam("refreshToken", tokenDTO.getRefreshToken())
                    .build().toUriString();
        }

        response.sendRedirect(targetUrl);
    }

    private boolean isTestProfile() {
        String profile = System.getProperty("spring.profiles.active");
        return "testapp".equals(profile);
    }
}
