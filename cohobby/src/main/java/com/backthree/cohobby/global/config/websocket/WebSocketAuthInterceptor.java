package com.backthree.cohobby.global.config.websocket;

import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.domain.user.repository.UserRepository;
import com.backthree.cohobby.global.config.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

/**
 * WebSocket 연결 시 JWT 토큰을 검증하고 사용자 인증 정보를 설정하는 인터셉터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // CONNECT 프레임에서 토큰 추출
            String token = extractToken(accessor);
            
            if (token != null && jwtService.validateToken(token)) {
                String email = jwtService.extractEmail(token);
                if (email != null) {
                    User user = userRepository.findByEmail(email)
                            .orElse(null);
                    
                    if (user != null) {
                        // 인증 정보 생성
                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        // WebSocket 세션에 사용자 정보 설정 (Principal로 설정)
                        UserPrincipal userPrincipal = new UserPrincipal(user);
                        accessor.setUser(userPrincipal);
                        
                        // 세션 속성에도 User 저장 (나중에 접근 가능하도록)
                        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                        if (sessionAttributes != null) {
                            sessionAttributes.put("user", user);
                        }
                        
                        log.debug("WebSocket 인증 성공: {}", email);
                    } else {
                        log.warn("WebSocket 인증 실패: 사용자를 찾을 수 없음 - {}", email);
                    }
                }
            } else {
                log.warn("WebSocket 인증 실패: 유효하지 않은 토큰");
            }
        }
        
        return message;
    }

    /**
     * STOMP 헤더나 쿼리 파라미터에서 JWT 토큰 추출
     */
    private String extractToken(StompHeaderAccessor accessor) {
        // 1. Authorization 헤더에서 추출 시도
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        // 2. 쿼리 파라미터에서 추출 시도 (WebSocket 연결 시)
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes != null) {
            Object tokenObj = sessionAttributes.get("token");
            if (tokenObj instanceof String) {
                String token = (String) tokenObj;
                if (token.startsWith("Bearer ")) {
                    return token.substring(7);
                }
                return token;
            }
        }
        
        return null;
    }
}

