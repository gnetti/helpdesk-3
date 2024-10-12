package com.luiz.helpdesk.domain.exception.tokenTime;

public class TokenTimeUpdateException extends RuntimeException {

    public TokenTimeUpdateException(String message) {
        super(message);
    }

    public TokenTimeUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenTimeUpdateException(Throwable cause) {
        super(cause);
    }

    protected TokenTimeUpdateException(String message, Throwable cause,
                                       boolean enableSuppression,
                                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}