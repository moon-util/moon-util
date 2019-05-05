package com.moon.core.exception;

/**
 * @author benshaoye
 */
public class MessageException extends RuntimeException {
    public MessageException() { }

    public MessageException(String message) { super(message); }

    public MessageException(String message, Throwable cause) { super(message, cause); }

    public MessageException(Throwable cause) { super(cause.getMessage(), cause); }

    public MessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
