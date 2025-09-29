package com.backthree.cohobby.global.config.swagger;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        servers = {
                @Server(url = "http://localhost:8080", description = "로컬 서버")
        })
public class SwaggerConfig {
    @Bean
    public OpenAPI catchyApi() {
        return new OpenAPI()
                .info(apiInfo())
                .components(authSetting())
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