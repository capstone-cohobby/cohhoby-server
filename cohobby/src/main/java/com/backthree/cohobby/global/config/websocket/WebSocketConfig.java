package com.backthree.cohobby.global.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트 접속 엔드포인트
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*");
        // SockJS 사용 클라이언트 지원
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트 -> 서버 publish prefix
        registry.setApplicationDestinationPrefixes("/pub");
        // 서버 -> 클라이언트 subscribe prefix
        registry.enableSimpleBroker("/sub");
    }
}
