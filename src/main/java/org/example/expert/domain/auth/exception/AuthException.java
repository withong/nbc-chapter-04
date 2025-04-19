package org.example.expert.domain.auth.exception;

import org.example.expert.common.exception.BaseException;
import org.example.expert.common.exception.ErrorCode;

public class AuthException extends BaseException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
