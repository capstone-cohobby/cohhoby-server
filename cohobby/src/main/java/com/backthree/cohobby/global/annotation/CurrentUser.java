package com.backthree.cohobby.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 현재 인증된 사용자를 주입받기 위한 어노테이션
 * 컨트롤러 메서드 파라미터에 사용
 * 
 * 사용 예시:
 * public ResponseEntity<?> someMethod(@CurrentUser User user) {
 *     Long userId = user.getId();
 *     // ...
 * }
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
}

