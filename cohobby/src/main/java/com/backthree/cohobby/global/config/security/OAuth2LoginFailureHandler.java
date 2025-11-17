package com.backthree.cohobby.global.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        
        log.error("OAuth2 로그인 실패: {}", exception.getMessage(), exception);
        
        // 에러 메시지를 로그에 상세히 기록
        log.error("에러 타입: {}", exception.getClass().getName());
        log.error("에러 원인: {}", exception.getCause() != null ? exception.getCause().getMessage() : "없음");
        
        // Rate Limit 에러 감지 및 처리
        String errorMessage = exception.getMessage() != null ? exception.getMessage() : "알 수 없는 오류가 발생했습니다.";
        String userFriendlyMessage = errorMessage;
        
        // Rate Limit 에러인 경우 사용자 친화적인 메시지로 변경
        if (errorMessage.contains("rate limit") || errorMessage.contains("KOE237") || errorMessage.contains("429")) {
            userFriendlyMessage = "요청이 너무 많습니다. 잠시 후(약 5-10분) 다시 시도해주세요.";
            log.warn("카카오 API Rate Limit 초과 - 사용자에게 대기 안내");
        }
        
        // 프론트엔드로 에러 정보 전달
        response.sendRedirect("http://localhost:3000/login?error=" + java.net.URLEncoder.encode(userFriendlyMessage, "UTF-8"));
    }
}

