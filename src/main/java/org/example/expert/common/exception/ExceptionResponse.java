package org.example.expert.common.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/**
 * 예외 응답 DTO - 예외 발생 시 반환되는 형식 정의
 */
@Getter
@Builder
public class ExceptionResponse {

    /**
     * HTTP 상태 코드 (예: 204, 400 등)
     */
    private int status;

    /**
     * 예외 코드 (예: NOT_FOUND_USER, INVALID_PASSWORD 등)
     */
    private String code;

    /**
     * 예외 메시지 (예: 비밀번호가 일치하지 않습니다.)
     */
    private String message;

    /**
     * 예외 응답 객체 생성
     *
     * @param e 예외 코드(ExceptionCode enum)
     * @return 예외 응답 객체를 담은 ResponseEntity
     */
    public static ResponseEntity<ExceptionResponse> dtoResponseEntity(ErrorCode e) {
        return ResponseEntity.status(e.getStatus().value())
                .body(ExceptionResponse.builder()
                        .status(e.getStatus().value())
                        .code(e.getCode())
                        .message(e.getMessage())
                        .build());
    }
}
