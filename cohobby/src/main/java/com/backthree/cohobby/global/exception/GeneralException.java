package com.backthree.cohobby.global.exception;

import com.backthree.cohobby.global.common.response.code.BaseCode;
import com.backthree.cohobby.global.common.response.code.ReasonDTO;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import lombok.Getter;


@Getter
public class GeneralException extends RuntimeException {

    private final BaseCode code;
    private final String additionalMessage; // 추가 메시지 필드

    // 기본 생성자 (추가 메시지 없이 사용)
    public GeneralException(BaseCode code) {
        super(code.getReason().getMessage());
        this.code = code;
        this.additionalMessage = null; // 추가 메시지는 null로 초기화
    }

    // 추가 메시지를 포함하는 생성자
    public GeneralException(BaseCode code, String additionalMessage) {
        super(code.getReason().getMessage()); //서버 내부 로그/스택 트레이스 디버깅 위한 필드
        this.code = code;
        this.additionalMessage = additionalMessage; //클라이언트에게 보여줄 응답 메시지
    }

    public ReasonDTO getReason() {
        return this.code.getReason();
    } //Getter 메서드: getReason으로 통일

}