package com.backthree.cohobby.global.exception;

import com.backthree.cohobby.global.common.response.code.BaseErrorCode;
import com.backthree.cohobby.global.common.response.code.ErrorReasonDTO;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import lombok.Getter;


@Getter
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;
    private String additionalMessage; // 추가 메시지 필드

    // 기본 생성자 (추가 메시지 없이 사용)
    public GeneralException(BaseErrorCode code) {
        super(code.getReason().getMessage());
        this.code = code;
        this.additionalMessage = null; // 추가 메시지는 null로 초기화
    }

    // 추가 메시지를 포함하는 생성자
    public GeneralException(BaseErrorCode code, String additionalMessage) {
        super(code.getReason().getMessage() + ": " + additionalMessage);
        this.code = code;
        this.additionalMessage = additionalMessage;
    }

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }

    public ErrorStatus getErrorStatus() {
        if (this.code instanceof ErrorStatus errorStatus) {
            return errorStatus;
        }
        return null;
    }
}