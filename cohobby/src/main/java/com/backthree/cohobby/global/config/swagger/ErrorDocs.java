package com.backthree.cohobby.global.config.swagger;

import com.backthree.cohobby.global.common.response.status.ErrorStatus;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Retention(RUNTIME)
@Target({ METHOD, TYPE })
public @interface ErrorDocs {
    ErrorStatus[] value() default {};
    boolean show400() default false;    // 기본 false → 전역 400 숨김
    boolean show500() default false;    // 기본 false → 전역 500 숨김

}
