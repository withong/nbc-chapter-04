package org.example.expert.common.exception;

public class ServerException extends BaseException {

    public ServerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
