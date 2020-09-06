package com.moon.data.web;

/**
 * @author moonsky
 */
public class RecordDowngradeBuildException extends RuntimeException {

    public RecordDowngradeBuildException() {}

    public RecordDowngradeBuildException(String message) { super(message); }

    public RecordDowngradeBuildException(String message, Throwable cause) { super(message, cause); }

    public RecordDowngradeBuildException(Throwable cause) { super(cause); }

    public RecordDowngradeBuildException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
    ) { super(message, cause, enableSuppression, writableStackTrace); }
}
