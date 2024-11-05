package com.example.dailyschedule.error.type;

import lombok.Getter;

@Getter
public enum ErrorCode {
    CREATION_FAILED("생성에 실패했습니다.", 400),
    UPDATE_FAILED("update에 실패했습니다.", 400),
    DELETE_FAILED("삭제에 실패했습니다.", 400),

    ID_EXIST("중복된 아이디 입니다. " , 409),
    USER_ID_EXIST("중복된 userId 입니다. " , 409),

    NOT_FOUND("조회에 실패했습니다.", 404),
    ID_NOT_FOUND("존재하지 않는 ID 입니다.", 404),
    DATE_NOT_FOUND("해당 날짜를 찾을 수 없습니다.", 404),
    INVALID_MEMBER_INFO("존재하지 않는 회원입니다.", 404),

    PASSWORD_INCORRECT("비밀번호가 일치하지 않습니다.", 401),
    ID_INCORRECT("ID 값이 일치하지 않습니다.", 401),

    INTERNAL_SERVER_ERROR("서버 에러 입니다, 서버팀에 연락 주세요!!", 500);

    private final String message;
    private final int status;

    ErrorCode(String message, int status) {
        this.message = message;
        this.status = status;
    }
}
