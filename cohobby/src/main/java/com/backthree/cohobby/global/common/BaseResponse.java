package com.backthree.cohobby.global.common;

import com.backthree.cohobby.global.common.response.code.BaseCode;
import com.backthree.cohobby.global.common.response.code.ErrorReasonDTO;
import com.backthree.cohobby.global.common.response.status.ErrorStatus;
import com.backthree.cohobby.global.common.response.status.SuccessStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;

@Getter
@AllArgsConstructor(access= AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@JsonPropertyOrder({"isSuccess","code","message","result"})
public class BaseResponse<T> {

    @JsonProperty("isSuccess")
    @Schema(description = "성공 여부")
    private final Boolean isSuccess;

    @Schema(description = "응답/에러 코드")
    private final String code;
    @Schema(description = "응답 데이터")
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public static <T> BaseResponse<T> onSuccess(SuccessStatus status, T data){
        return new BaseResponse<>(true,status.getCode(), status.getMessage(), data);
    }
    public static <T> BaseResponse<T> of(BaseCode code, T result) {
        return new BaseResponse<>(
                true,
                code.getReasonHttpStatus().getCode(),
                code.getReasonHttpStatus().getMessage(),
                result);
    }

    public static <T> BaseResponse<T> onFailure(ErrorStatus errorCode, T data) {
        return new BaseResponse<>(false, errorCode.getCode(), errorCode.getMessage(), data);
    }

    public static <T> BaseResponse<T> onFailure(ErrorStatus errorCode) {
        return onFailure(errorCode, null);
    }

    public static <T> BaseResponse<T> onFailure(ErrorReasonDTO reason, T data) {
        return new BaseResponse<>(false, reason.getCode(), reason.getMessage(), data);
    }

    public static BaseResponse<Object> onFailureWithEmptyList(ErrorStatus errorStatus) {
        return new BaseResponse<>(false, errorStatus.getCode(), errorStatus.getMessage(), Collections.emptyList());
    }
}
