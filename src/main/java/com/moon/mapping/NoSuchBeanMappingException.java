package com.moon.mapping;

/**
 * @author benshaoye
 */
public class NoSuchBeanMappingException extends IllegalStateException {

    public NoSuchBeanMappingException(String fromClass, String toClass) {
        super("没有从 " + fromClass + " 到 " + toClass + " 的映射器.");
    }

    public NoSuchBeanMappingException(Class<?> fromClass, Class<?> toClass) {
        this(fromClass.getName(), toClass.getName());
    }
}
