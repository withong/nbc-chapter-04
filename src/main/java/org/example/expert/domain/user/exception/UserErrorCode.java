package org.example.expert.domain.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.expert.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {

    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "USER_ERROR_001", "이미 존재하는 이메일입니다."),
    NOT_REGISTERED_USER(HttpStatus.NOT_FOUND, "USER_ERROR_002", "가입되지 않은 사용자입니다."),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "USER_ERROR_003", "유효하지 않은 사용자 권한입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_ERROR_004", "사용자가 존재하지 않습니다."),
    DUPLICATE_NEW_PASSWORD(HttpStatus.BAD_REQUEST, "USER_ERROR_005", "새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
