package com.backthree.cohobby.global.exception;

import com.backthree.cohobby.global.common.BaseResponse;
import com.backthree.cohobby.global.common.response.code.ErrorReasonDTO;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity<BaseResponse<Object>> handleGeneralException(GeneralException e) {
        // 1. 발생한 예외에서 에러 코드와 메시지 정보를 가져오기
        ErrorReasonDTO errorReason = e.getErrorReasonHttpStatus();

        // 2. BaseResponse.onFailure를 사용해 실패 응답을 생성
        BaseResponse<Object> response = BaseResponse.onFailure(e.getCode().getReason(), null);

        // 3. ResponseEntity에 BaseResponse와 예외에 정의된 HTTP 상태 코드를 담아 반환
        return ResponseEntity.status(errorReason.getHttpStatus()).body(response);
    }

    // 잘못된 JSON 요청을 처리하기 위한 메소드 오버라이드
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        BaseResponse<Object> response = BaseResponse.onFailure(ErrorStatus.BAD_REQUEST);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 👇 @Valid 유효성 검사 실패 시 처리하기 위한 메소드 오버라이드
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        BaseResponse<Object> response = BaseResponse.onFailure(ErrorStatus.VALIDATION_ERROR);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 예상치 못한 모든 예외를 처리
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleException(Exception e) {
        // 서버 에러이므로 HTTP 500과 함께 정해진 코드를 반환
        BaseResponse<Object> response = BaseResponse.onFailure(ErrorStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
