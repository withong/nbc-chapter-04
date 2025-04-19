package org.example.expert.domain.comment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.expert.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommentErrorCode implements ErrorCode {

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_ERROR_001", "댓글이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
