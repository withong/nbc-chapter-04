package org.example.expert.common.client.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.expert.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ClientErrorCode implements ErrorCode {

    FAIL_GET_WEATHER(HttpStatus.INTERNAL_SERVER_ERROR, "CLIENT_ERROR_001", "날씨 데이터를 가져오는데 실패했습니다."),
    EMPTY_WEATHER_DATA(HttpStatus.INTERNAL_SERVER_ERROR, "CLIENT_ERROR_002", "날씨 데이터가 없습니다."),
    NOT_FOUND_TODAY_WEATHER(HttpStatus.INTERNAL_SERVER_ERROR, "CLIENT_ERROR_003", "오늘에 해당하는 날씨 데이터를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
