package com.backthree.cohobby.global.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
            ) throws ServletException, IOException {
        // URL 확인용 로그 (어떤 요청이 들어왔는지)
        log.info("🔍 [Filter Start] 요청 URL: {} {}", request.getMethod(), request.getRequestURI());
        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            filterChain.doFilter(request, response);
            return;
        }

        //1. 헤더에서 Authorization 값을 찾는다
        final String authorizationHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String email;
        //2. 헤더가 없거나 Bearer로 시작하지 않으면 요청을 통과시킴(로그인 안 한 사용자라는 뜻)
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            // 3. Bearer 부분을 잘라내고 순수 토큰(jwt)만 추출
            jwtToken = authorizationHeader.substring(7);

            // [중요] 여기서 예외가 발생하면 catch로 넘어갑니다 (서버 재시작으로 인한 서명 불일치 등)
            email = jwtService.extractEmail(jwtToken);

            // 5. 이메일이 있고 아직 인증 정보가 없는 사용자인지 검증
            if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // 7. 토큰 유효성 검증
                if(jwtService.validateToken(jwtToken) && userDetails.isEnabled()) {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("인증 성공: {}", email);
                }
            }
        } catch (Exception e) {
            // [핵심] 에러 로그를 여기서 확인해야 합니다!
            log.error("JWT 필터 처리 중 에러 발생: {}", e.getMessage());
            // e.printStackTrace(); // 필요하다면 상세 스택트레이스 출력

            // 에러가 났더라도 doFilter를 호출하여 Spring Security가 401/403을 적절히 처리하게 하거나,
            // 여기서 명시적으로 401 응답을 보낼 수도 있습니다.
            // 일단은 다음 필터로 넘겨서 "인증되지 않은 사용자"로 처리되게 합니다.
        }

        //9. 필터로 요청/응답을 넘김
        filterChain.doFilter(request, response);
    }
}
