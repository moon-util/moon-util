package com.moon.data.registry;

/**
 * @author moonsky
 */
public class RecordDuplicateRegistryException extends RuntimeException {

    public RecordDuplicateRegistryException() { this("不能重复注册实体"); }

    public RecordDuplicateRegistryException(Class clazz) { this(String.format("不能重复注册：%s", clazz)); }

    public RecordDuplicateRegistryException(String message) { super(message); }

    public RecordDuplicateRegistryException(String message, Throwable cause) { super(message, cause); }

    public RecordDuplicateRegistryException(Throwable cause) { super(cause); }

    public RecordDuplicateRegistryException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
    ) { super(message, cause, enableSuppression, writableStackTrace); }
}
