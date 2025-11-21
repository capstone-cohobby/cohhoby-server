package com.backthree.cohobby.global.config.websocket;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

/**
 * WebSocket 핸드셰이크 시 쿼리 파라미터에서 JWT 토큰을 추출하여 세션에 저장
 */
@Slf4j
@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            
            // 쿼리 파라미터에서 토큰 추출
            URI uri = request.getURI();
            String query = uri.getQuery();
            
            if (query != null) {
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2 && "token".equals(keyValue[0])) {
                        String token = keyValue[1];
                        // 세션 속성에 토큰 저장 (나중에 ChannelInterceptor에서 사용)
                        attributes.put("token", token);
                        log.debug("WebSocket 핸드셰이크: 토큰 추출 성공");
                        break;
                    }
                }
            }
            
            // Authorization 헤더에서도 토큰 추출 시도
            String authHeader = request.getHeaders().getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                attributes.put("token", authHeader);
                log.debug("WebSocket 핸드셰이크: Authorization 헤더에서 토큰 추출");
            }
        }
        
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 핸드셰이크 완료 후 처리 (필요시)
    }
}

