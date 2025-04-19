package org.example.expert.domain.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.expert.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    MISSING_AUTH_ANNOTATION(HttpStatus.UNAUTHORIZED, "AUTH_ERROR_001", "@Auth와 AuthUser 타입은 함께 사용되어야 합니다."),
    NOT_FOUND_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_ERROR_002", "토큰이 존재하지 않습니다."),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH_ERROR_003", "잘못된 비밀번호입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
