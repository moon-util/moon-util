package com.moon.spring.jpa.web;

/**
 * @author moonsky
 */
public class BaseSettingsException extends RuntimeException{

    public BaseSettingsException() {}

    public BaseSettingsException(String message) {
        super(message);
    }

    public BaseSettingsException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseSettingsException(Throwable cause) {
        super(cause);
    }

    public BaseSettingsException(
        String message,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
