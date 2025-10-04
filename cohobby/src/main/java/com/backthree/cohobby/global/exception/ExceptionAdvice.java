package com.backthree.cohobby.global.exception;

import com.backthree.cohobby.global.common.BaseResponse;
import com.backthree.cohobby.global.common.response.code.ReasonDTO;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j //로깅 따로 기록하기 위한 어노테이션
@RestControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    // GeneralException 처리
    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity<BaseResponse<Object>> handleGeneralException(GeneralException e) {
        // 1. 발생한 예외에서 에러 코드와 메시지 정보를 가져오기
        ReasonDTO reason = e.getReason();
        String message = reason.getMessage();
        // 1-2. 추가 메시지가 있는 경우 응답 메시지에 추가
        if(e.getAdditionalMessage() != null && !e.getAdditionalMessage().isBlank()) {
            message = message + " - " + e.getAdditionalMessage();
        }

        // 2. BaseResponse.onFailure를 사용해 실패 응답을 생성
        BaseResponse<Object> response = BaseResponse.onFailure(reason.getCode(), message, null);

        // 3. ResponseEntity에 BaseResponse와 예외에 정의된 HTTP 상태 코드를 담아 반환
        return ResponseEntity.status(reason.getHttpStatus()).body(response);
    }

    // 잘못된 JSON 요청을 처리하기 위한 메소드 오버라이드
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.warn("HttpMessageNotReadableException: {}", ex.getMessage());
        ReasonDTO reason = ErrorStatus.BAD_REQUEST.getReason();
        BaseResponse<Object> response = BaseResponse.onFailure(reason.getCode(), reason.getMessage(), null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 👇 @Valid 유효성 검사 실패 시 처리하기 위한 메소드 오버라이드
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        //발생한 모든 에러의 상세정보를 필드별로 반환
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ReasonDTO reason = ErrorStatus.VALIDATION_ERROR.getReason();
        BaseResponse<Object> response = BaseResponse.onFailure(reason.getCode(), reason.getMessage(), errors);

        return ResponseEntity.status(reason.getHttpStatus()).body(response);
    }

    // 예상치 못한 모든 예외를 처리
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleException(Exception e) {
        log.error("Unhandled exception:", e); // 서버 에러 로그 기록
        // 서버 에러이므로 HTTP 500과 함께 정해진 코드를 반환
        ReasonDTO reason = ErrorStatus.INTERNAL_SERVER_ERROR.getReason();
        BaseResponse<Object> response = BaseResponse.onFailure(reason.getCode(), reason.getMessage(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
