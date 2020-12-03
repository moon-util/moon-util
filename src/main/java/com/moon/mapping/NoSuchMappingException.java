package com.moon.mapping;

/**
 * @author benshaoye
 */
public class NoSuchMappingException extends IllegalStateException {

    public NoSuchMappingException(String fromClass, String toClass) {
        super("没有从 " + fromClass + " 到 " + toClass + " 的映射器.");
    }

    public NoSuchMappingException(String fromClass, String toClass, Throwable t) {
        super("没有从 " + fromClass + " 到 " + toClass + " 的映射器.", t);
    }
}
