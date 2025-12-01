package com.backthree.cohobby.global.common.response.status;

import com.backthree.cohobby.global.common.response.code.BaseCode;
import com.backthree.cohobby.global.common.response.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {
    // 기본 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    INVALID_REQUEST_INFO(HttpStatus.BAD_REQUEST, "COMMON404", "요청된 정보가 올바르지 않습니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "COMMON405", "유효성 검증에 실패했습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON405", "유효하지 않은 파라미터입니다."),

    //user 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4041", "유저를 찾을 수 없습니다"),

    //hobby 관련 에러
    HOBBY_NOT_FOUND(HttpStatus.NOT_FOUND,"HOBBY4041", "취미를 찾을 수 없습니다"),

    //post 관련 에러
    POST_NOT_FOUND(HttpStatus.NOT_FOUND,"POST4041", "post를 찾을 수 없습니다"),
    POST_STATUS_CONFLICT(HttpStatus.CONFLICT,"POST4091","잘못된 post status"),
    POST_AUTHOR_MISMATCH(HttpStatus.FORBIDDEN,"POST4031","자신이 작성한 post가 아닙니다"),

    //post 422
    AVAILABLE_PERIOD_INVALID(HttpStatus.UNPROCESSABLE_ENTITY,"POST4221", "대여 가능 기간이 올바르지 않습니다."),
    PURCHASE_DATE_IN_FUTURE(HttpStatus.UNPROCESSABLE_ENTITY,"POST4221", "구입일은 미래일 수 없습니다."),

    // 이미지 관련 에러
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "FILE4001", "잘못된 파일 확장자입니다."),
    NOT_IMAGE_FILE(HttpStatus.BAD_REQUEST, "FILE4002", "이미지 파일만 업로드 가능합니다."),
    S3_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "FILE5001", "이미지 업로드 중 소버 오류가 발생했습니다."),

    //ai 리포트 관련 에러
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "AI4041","Ai 리포트를 조회할 수 없습니다."),
    // 찜 관련 에러
    LIKE_ALREADY_EXISTS(HttpStatus.CONFLICT, "LIKE4091", "이미 찜한 게시물입니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "LIKE4041", "찜한 내역을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() { //getReason 메서드에 모든 정보 포함되도록 통일
        return ReasonDTO.builder()
                .httpStatus(this.httpStatus)
                .isSuccess(false)
                .code(this.code)
                .message(this.message)
                .build();
    }

}
