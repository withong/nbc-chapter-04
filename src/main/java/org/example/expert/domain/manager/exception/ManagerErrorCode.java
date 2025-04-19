package org.example.expert.domain.manager.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.expert.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ManagerErrorCode implements ErrorCode {

    MANAGER_NOT_FOUND(HttpStatus.NOT_FOUND, "MANAGER_ERROR_001", "등록하려고 하는 담당자 사용자가 존재하지 않습니다."),
    INVALID_MANAGER_REQUESTER(HttpStatus.BAD_REQUEST, "MANAGER_ERROR_002", "담당자를 등록하려고 하는 사용자나 일정을 만든 사용자가 유효하지 않습니다."),
    INVALID_MANAGER_SELF_ASSIGN(HttpStatus.BAD_REQUEST, "MANAGER_ERROR_003", "일정 작성자는 본인을 담당자로 등록할 수 없습니다."),
    NOT_MANAGER_OF_TODO(HttpStatus.FORBIDDEN, "MANAGER_ERROR_004", "해당 일정에 등록된 담당자가 아닙니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
