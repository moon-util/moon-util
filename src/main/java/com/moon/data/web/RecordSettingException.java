package com.moon.data.web;

/**
 * @author moonsky
 */
public class RecordSettingException extends RuntimeException {

    public RecordSettingException() {}

    public RecordSettingException(String message) { super(message); }

    public RecordSettingException(String message, Throwable cause) { super(message, cause); }

    public RecordSettingException(Throwable cause) { super(cause); }

    public RecordSettingException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
    ) { super(message, cause, enableSuppression, writableStackTrace); }
}
