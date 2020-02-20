package com.moon.core.exception;

/**
 * 和{@link IllegalArgumentException}一样，不过已经被各框架广泛使用
 *
 * @author benshaoye
 * @see IllegalArgumentException
 */
public class IllegalParameterException extends RuntimeException {

    public IllegalParameterException(Object object) {
        this(String.valueOf(object));
    }

    public IllegalParameterException(String message) {
        super(message);
    }

    public IllegalParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalParameterException(Throwable cause) {
        super(cause);
    }

    protected IllegalParameterException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public IllegalParameterException() {}
}
