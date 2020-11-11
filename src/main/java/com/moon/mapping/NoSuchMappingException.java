package com.moon.mapping;

/**
 * @author benshaoye
 */
public class NoSuchMappingException extends IllegalStateException {

    public NoSuchMappingException(String fromClass, String toClass) {
        super("没有从 " + fromClass + " 到 " + toClass + " 的映射器.");
    }

    public NoSuchMappingException(Class<?> fromClass, Class<?> toClass) {
        this(fromClass.getName(), toClass.getName());
    }

    public NoSuchMappingException(String classname, Throwable cause) {
        super("找不到映射器: " + classname, cause);
    }
}
