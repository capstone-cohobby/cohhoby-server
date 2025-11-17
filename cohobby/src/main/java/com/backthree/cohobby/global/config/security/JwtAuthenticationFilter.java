package com.backthree.cohobby.global.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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

        //1. 헤더에서 Authorization 값을 찾는다
        final String authorizationHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String email;
        //2. 헤더가 없거나 Bearer로 시작하지 않으면 요청을 통과시킴(로그인 안 한 사용자라는 뜻)
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        //3. Bearer 부분을 잘라내고 순수 토큰(jwt)만 추출
        jwtToken = authorizationHeader.substring(7);
        //4. 토큰에서 이메일 추출
        email = jwtService.extractEmail(jwtToken);
        //5. 이메일이 있고 아직 인증 정보가 없는 사용자인지 검증
        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //6. db에서 사용자 정보 가져옴(db 조회가 1번만 이루어지도록 UserDetails로 가져오기)
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            //7. 토큰이 유효하고 db에도 사용자가 있으면 공식 인증서(Authentication)을 만듦
            if(jwtService.validateToken(jwtToken) && userDetails.isEnabled()) {
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()); //userdetails에서 직접 권한 목록을 가져옴
                //8. Spring security의 보안 컨텍스트에 이 인증서를 등록
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        //9. 필터로 요청/응답을 넘김
        filterChain.doFilter(request, response);
    }
}
