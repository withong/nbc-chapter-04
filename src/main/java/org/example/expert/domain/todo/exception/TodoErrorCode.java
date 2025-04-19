package org.example.expert.domain.todo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.expert.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TodoErrorCode implements ErrorCode {

    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "TODO_ERROR_001", "일정이 존재하지 않습니다."),
    INVALID_TODO_OWNER(HttpStatus.BAD_REQUEST, "TODO_ERROR_002", "해당 일정을 만든 유저가 유효하지 않습니다.");;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
