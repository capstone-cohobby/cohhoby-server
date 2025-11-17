package com.backthree.cohobby.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://127.0.0.1:3000",
                        "http://43.203.228.76:3000",   // 배포된 FE 주소
                )
                .allowedMethods("*")   // GET, POST, PUT, DELETE 등
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
