package com.backthree.cohobby.global.config.swagger;

import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class OperationErrorDocCustomizer {

    @Bean
    public OperationCustomizer errorDocCustomizer() {
        return (operation, handlerMethod) -> {
            ErrorDocs classAnn  = handlerMethod.getBeanType().getAnnotation(ErrorDocs.class);
            ErrorDocs methodAnn = handlerMethod.getMethodAnnotation(ErrorDocs.class);

            List<ErrorStatus> declared = new ArrayList<>();
            if (classAnn  != null) Collections.addAll(declared, classAnn.value());
            if (methodAnn != null) Collections.addAll(declared, methodAnn.value());
            if (declared.isEmpty()) return operation;

            // 400/500 노출 플래그 (메서드 > 클래스 우선)
            final boolean show400 =
                    (methodAnn != null) ? methodAnn.show400()
                            : (classAnn != null && classAnn.show400());
            final boolean show500 =
                    (methodAnn != null) ? methodAnn.show500()
                            : (classAnn != null && classAnn.show500());

            // 400/500 기본 숨김 (스위치가 true일 때만 노출)
            declared.removeIf(es -> {
                int sc = es.getHttpStatus().value();
                return (sc == 400 && !show400) || (sc == 500 && !show500);
            });
            if (declared.isEmpty()) return operation;

            Map<Integer, List<ErrorStatus>> byStatus =
                    declared.stream().collect(Collectors.groupingBy(es -> es.getHttpStatus().value()));

            ApiResponses responses = operation.getResponses();

            byStatus.forEach((statusCode, statuses) -> {
                String key = String.valueOf(statusCode);
                if (responses.containsKey(key)) return; // 이미 있으면 덮어쓰지 않음

                // Description: enum 이름 목록
                String desc = statuses.stream()
                        .map(ErrorStatus::name)
                        .distinct()
                        .collect(Collectors.joining(" | "));

                // 스키마 + 예제 자동 생성
                MediaType media = new MediaType()
                        .schema(new Schema<>().$ref("#/components/schemas/ErrorResponseDto"));

                for (ErrorStatus s : statuses) {
                    Map<String, Object> value = new LinkedHashMap<>();
                    value.put("isSuccess", false);
                    value.put("code", s.getCode());
                    value.put("message", s.getMessage());

                    Example ex = new Example();
                    ex.setSummary(s.name());   // 예제 이름: POST_NOT_FOUND 등
                    ex.setValue(value);
                    media.addExamples(s.name(), ex);
                }

                ApiResponse apiResp = new ApiResponse()
                        .description(desc)
                        .content(new Content().addMediaType("application/json", media));

                responses.addApiResponse(key, apiResp);
            });

            return operation;
        };
    }
}