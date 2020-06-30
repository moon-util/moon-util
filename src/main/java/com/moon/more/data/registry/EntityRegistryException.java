package com.moon.more.data.registry;

/**
 * @author moonsky
 */
public class EntityRegistryException extends RuntimeException {

    public EntityRegistryException() { this("不能重复注册实体"); }

    public EntityRegistryException(Class clazz) { this(String.format("不能重复注册：%s", clazz)); }

    public EntityRegistryException(String message) { super(message); }

    public EntityRegistryException(String message, Throwable cause) { super(message, cause); }

    public EntityRegistryException(Throwable cause) { super(cause); }

    public EntityRegistryException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
    ) { super(message, cause, enableSuppression, writableStackTrace); }
}
