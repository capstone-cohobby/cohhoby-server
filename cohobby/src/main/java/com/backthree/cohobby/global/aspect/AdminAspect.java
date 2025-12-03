package com.backthree.cohobby.global.aspect;

import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.global.annotation.RequireAdmin;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AdminAspect {

    @Before("@annotation(com.backthree.cohobby.global.annotation.RequireAdmin)")
    public void checkAdmin(JoinPoint joinPoint) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || authentication.getPrincipal() == null) {
            log.warn("인증되지 않은 사용자가 관리자 전용 API에 접근했습니다.");
            throw new IllegalStateException("로그인이 필요합니다!");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            log.warn("Principal이 User 타입이 아닙니다.");
            throw new IllegalStateException("로그인이 필요합니다!");
        }

        User user = (User) principal;
        if (!user.isAdmin()) {
            log.warn("일반 사용자가 관리자 전용 API에 접근했습니다. userId={}", user.getId());
            throw new IllegalStateException("관리자 권한이 필요합니다!");
        }
    }
}

