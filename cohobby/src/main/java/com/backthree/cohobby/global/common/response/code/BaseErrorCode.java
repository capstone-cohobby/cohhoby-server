package com.backthree.cohobby.global.common.response.code;

public interface BaseErrorCode {
    ErrorReasonDTO getReason();

    ErrorReasonDTO getReasonHttpStatus();
}
