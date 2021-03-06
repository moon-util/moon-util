package com.moon.mapping;

/**
 * @author benshaoye
 */
public class NoSuchMapperException extends IllegalStateException {

    public NoSuchMapperException(String fromClass, String toClass, Throwable t) {
        super("没有从 " + fromClass + " 到 " + toClass + " 的映射器.", t);
    }
}
