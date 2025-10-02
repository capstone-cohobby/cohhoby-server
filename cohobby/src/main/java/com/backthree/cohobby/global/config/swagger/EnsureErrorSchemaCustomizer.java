package com.backthree.cohobby.global.config.swagger;


import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Optional;
import org.springdoc.core.customizers.OpenApiCustomizer;
// 스키마 보증 커스터마이저
@Configuration
public class EnsureErrorSchemaCustomizer {

    @Bean
    public OpenApiCustomizer ensureErrorResponseDtoSchema() {
        return openApi -> {
            // components/schemas에 ErrorResponseDto 없으면 등록
            var comps = Optional.ofNullable(openApi.getComponents())
                    .orElseGet(() -> { var c = new Components(); openApi.setComponents(c); return c; });

            var schemas = Optional.ofNullable(comps.getSchemas())
                    .orElseGet(() -> { var m = new LinkedHashMap<String, io.swagger.v3.oas.models.media.Schema>();
                        comps.setSchemas(m); return m; });

            if (!schemas.containsKey("ErrorResponseDto")) {
                io.swagger.v3.core.converter.ModelConverters.getInstance()
                        .read(com.backthree.cohobby.global.common.ErrorResponseDto.class)
                        .forEach(comps::addSchemas);
            }
        };
    }
}
