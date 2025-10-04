package com.backthree.cohobby.global.config.swagger;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@OpenAPIDefinition(
        servers = {
                @Server(url = "http://localhost:8080", description = "로컬 서버")
        })
public class SwaggerConfig {
    @Bean
    public OpenAPI cohobbyApi() {
        //에러 응답 DTO 스키마 추가 - 기존 SchemaCustomizer 대신 사용
        Schema<?> errorResponseSchema = new Schema<>()
                .type("object")
                .properties(Map.of(
                        "isSuccess", new Schema<>().type("boolean").example(false),
                        "code", new Schema<>().type("string").example("ERROR_CODE"),
                        "message", new Schema<>().type("string").example("에러 메시지"),
                        "result", new Schema<>().type("object").nullable(true).example(null)
                ));

        //기존 authSetting 컴포넌트 설정에 스키마 추가
        Components components = authSetting()
                .addSchemas("ErrorResponse", errorResponseSchema);
        return new OpenAPI()
                .info(apiInfo())
                .components(components) // 수정한 컴포넌트 적용
                .addSecurityItem(securityRequirement());
    }

    private Info apiInfo() {
        return new Info()
                .title("CoHobby")
                .description("Cohobby API 명세서")
                .version("1.0.0");
    }

    SecurityScheme accessTokenSecurityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");

    SecurityScheme refreshTokenSecurityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.APIKEY)
            .in(SecurityScheme.In.HEADER)
            .name("Refresh-Token");

    private Components authSetting() {
        return new Components()
                .addSecuritySchemes("accessToken", accessTokenSecurityScheme)
                .addSecuritySchemes("refreshToken", refreshTokenSecurityScheme);
    }

    private SecurityRequirement securityRequirement() {
        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList("accessToken");
        securityRequirement.addList("refreshToken");
        return securityRequirement;
    }
}