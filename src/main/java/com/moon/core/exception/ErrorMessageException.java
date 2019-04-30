package com.moon.core.exception;

/**
 * @author benshaoye
 */
public class ErrorMessageException extends RuntimeException {
    public ErrorMessageException() {
    }

    public ErrorMessageException(String message) {
        super(message);
    }

    public ErrorMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorMessageException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public ErrorMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
