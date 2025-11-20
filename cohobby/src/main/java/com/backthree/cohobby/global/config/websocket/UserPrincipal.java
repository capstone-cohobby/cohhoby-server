package com.backthree.cohobby.global.config.websocket;

import com.backthree.cohobby.domain.user.entity.User;

import java.security.Principal;

/**
 * WebSocket에서 User를 Principal로 사용하기 위한 래퍼 클래스
 */
public class UserPrincipal implements Principal {
    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public String getName() {
        return user.getEmail();
    }

    public User getUser() {
        return user;
    }
}

