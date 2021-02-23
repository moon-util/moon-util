package com.moon.accessor.exception;

/**
 * @author benshaoye
 */
public class InitException extends RuntimeException {

    public InitException(String message) {
        super(message);
    }

    public InitException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitException(Throwable cause) {
        super(cause);
    }
}
