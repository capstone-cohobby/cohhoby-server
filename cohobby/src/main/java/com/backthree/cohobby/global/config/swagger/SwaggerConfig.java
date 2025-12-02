package com.backthree.cohobby.global.config.swagger;


import com.backthree.cohobby.domain.user.entity.User;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@OpenAPIDefinition(
        servers = {
                @Server(url = "http://localhost:8080", description = "로컬 서버")
        })
public class SwaggerConfig {
    @Bean
    @SuppressWarnings("unchecked")
    public OpenAPI cohobbyApi() {
        //에러 응답 DTO 스키마 추가 - 기존 SchemaCustomizer 대신 사용
        Schema<Object> isSuccessSchema = new Schema<Object>().type("boolean").example(false);
        Schema<Object> codeSchema = new Schema<Object>().type("string").example("ERROR_CODE");
        Schema<Object> messageSchema = new Schema<Object>().type("string").example("에러 메시지");
        Schema<Object> resultSchema = new Schema<Object>().type("object").nullable(true).example(null);
        
        Schema<Object> errorResponseSchema = new Schema<Object>()
                .type("object")
                .addProperty("isSuccess", isSuccessSchema)
                .addProperty("code", codeSchema)
                .addProperty("message", messageSchema)
                .addProperty("result", resultSchema);

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

    static{
        SpringDocUtils.getConfig().addRequestWrapperToIgnore(User.class);
    }
}