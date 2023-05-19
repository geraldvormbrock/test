package com.gvormbrock.test.exception;

import lombok.Getter;

@Getter
public class ErrorServerException extends RuntimeException {
    protected final int errorCode;

    public ErrorServerException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }
}
