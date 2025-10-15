package com.backthree.cohobby.global.common.response.status;

import com.backthree.cohobby.global.common.response.code.BaseCode;
import com.backthree.cohobby.global.common.response.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),
    _CREATED(HttpStatus.CREATED, "COMMON201", "요청 성공 및 리소스 생성됨"),
    _NO_CONTENT(HttpStatus.NO_CONTENT, "COMMON202", "요청 성공 및 반환할 콘텐츠가 없음");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason(){ //getReason 메서드에 모든 정보 담도록 통일

        return ReasonDTO.builder()
                .httpStatus(this.httpStatus)
                .isSuccess(true)
                .code(this.code)
                .message(this.message)
                .build();
    }

}
