package com.moon.data.registry;

/**
 * @author moonsky
 */
public class RecordRegistryException extends RuntimeException {

    public RecordRegistryException() { this("不能重复注册实体"); }

    public RecordRegistryException(Class clazz) { this(String.format("不能重复注册：%s", clazz)); }

    public RecordRegistryException(String message) { super(message); }

    public RecordRegistryException(String message, Throwable cause) { super(message, cause); }

    public RecordRegistryException(Throwable cause) { super(cause); }

    public RecordRegistryException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
    ) { super(message, cause, enableSuppression, writableStackTrace); }
}
