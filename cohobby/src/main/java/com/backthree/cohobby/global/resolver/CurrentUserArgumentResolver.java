package com.backthree.cohobby.global.resolver;

import com.backthree.cohobby.domain.user.entity.User;
import com.backthree.cohobby.global.annotation.CurrentUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @CurrentUser 어노테이션이 붙은 파라미터에 현재 인증된 User 객체를 주입하는 Resolver
 * 인증이 안되어 있으면 예외 발생
 */
@Slf4j
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @CurrentUser 어노테이션이 있고, User 타입인 경우에만 처리
        return parameter.hasParameterAnnotation(CurrentUser.class) &&
               parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        
        // SecurityContext에서 Authentication 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 인증이 안되어 있거나 principal이 없으면 예외 발생
        if (authentication == null || authentication.getPrincipal() == null) {
            log.warn("인증되지 않은 사용자가 인증이 필요한 API에 접근했습니다. 요청 URI: {}", 
                    webRequest.getNativeRequest(jakarta.servlet.http.HttpServletRequest.class).getRequestURI());
            throw new IllegalStateException("로그인이 필요합니다!");
        }
        
        // principal이 User 타입인지 확인
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            log.warn("Principal이 User 타입이 아닙니다. 타입: {}", principal.getClass().getName());
            throw new IllegalStateException("로그인이 필요합니다!");
        }
        
        return (User) principal;
    }
}

