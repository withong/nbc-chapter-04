package org.example.expert.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 사용자 정의 예외 처리
     *
     * @param e BaseException
     * @return 예외 응답
     */
    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<ExceptionResponse> handleCustomException(BaseException e) {
        return ExceptionResponse.dtoResponseEntity(e.getErrorCode());
    }

    /**
     * 유효성 검사 예외 처리
     *
     * @param e MethodArgumentNotValidException
     * @return 예외 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String code = "VALIDATION_FAILED";
        String message = e.getBindingResult().getFieldError().getDefaultMessage();

        return ResponseEntity.status(status.value())
                .body(ExceptionResponse.builder()
                        .status(status.value())
                        .code(code)
                        .message(message)
                        .build());
    }
}

