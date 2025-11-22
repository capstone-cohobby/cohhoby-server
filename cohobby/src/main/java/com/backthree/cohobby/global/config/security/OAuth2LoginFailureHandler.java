package com.backthree.cohobby.global.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

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
        
        // 현재 요청의 호스트와 포트를 동적으로 가져오기
        String scheme = request.getScheme(); // http 또는 https
        String serverName = request.getServerName(); // 호스트명 또는 IP
        int serverPort = request.getServerPort(); // 포트 번호
        
        // 포트가 기본 포트(80, 443)가 아니면 포트 번호 포함
        String baseUrl = (serverPort == 80 || serverPort == 443) 
            ? scheme + "://" + serverName 
            : scheme + "://" + serverName + ":" + serverPort;
        
        // 요청이 어디서 왔는지 확인 (Swagger인지 프론트엔드인지)
        String referer = request.getHeader("Referer");
        String redirectTo = request.getParameter("redirect_to");
        String requestPath = request.getRequestURI();
        
        // Swagger에서 온 경우 또는 redirect_to가 Swagger인 경우
        boolean isFromSwagger = (referer != null && (referer.contains("swagger-ui") || referer.contains("/docs/"))) ||
                                (redirectTo != null && (redirectTo.contains("swagger-ui") || redirectTo.contains("/docs/"))) ||
                                (requestPath != null && (requestPath.contains("swagger") || requestPath.contains("/docs/")));
        
        if (isFromSwagger) {
            // Swagger 경로 결정
            String swaggerPath = (referer != null && referer.contains("/docs/")) || 
                                (redirectTo != null && redirectTo.contains("/docs/")) ||
                                (requestPath != null && requestPath.contains("/docs/"))
                ? "/docs/swagger-ui/index.html" 
                : "/swagger-ui/index.html";
            
            // Swagger로 리다이렉트 (에러 메시지 포함, 동적 URL 사용)
            String swaggerUrl = UriComponentsBuilder.fromUriString(baseUrl + swaggerPath)
                    .queryParam("error", userFriendlyMessage)
                    .build().toUriString();
            response.sendRedirect(swaggerUrl);
        } else {
            // 프론트엔드로 에러 정보 전달 (동적 URL 사용)
            // 프론트엔드 URL은 환경 변수나 설정에서 가져오는 것이 좋지만, 일단 기본값 사용
            String frontendUrl = baseUrl.replace(":8080", ":3000"); // 서버 포트를 프론트 포트로 변경
            response.sendRedirect(frontendUrl + "/login?error=" + java.net.URLEncoder.encode(userFriendlyMessage, "UTF-8"));
        }
    }
}

