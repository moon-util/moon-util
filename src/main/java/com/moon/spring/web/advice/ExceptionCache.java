package com.moon.spring.web.advice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author moonsky
 */
class ExceptionCache {

    private final static Map<String, RestExceptionHandler> CACHE = new HashMap<>();

    /**
     * 注册异常处理器
     *
     * @param exceptionType 处理的异常类型
     * @param handler       异常处理器
     *
     * @see RestExceptionEnum
     */
    public final static void registry(Class exceptionType, RestExceptionHandler handler) {
        registry(exceptionType.getName(), handler);
    }

    /**
     * 注册异常处理器
     *
     * @param exceptionFullName 处理的异常名称
     * @param handler           异常处理器
     *
     * @see RestExceptionEnum
     */
    public final static void registry(String exceptionFullName, RestExceptionHandler handler) {
        CACHE.put(exceptionFullName, handler);
    }

    /**
     * 如果不存在指定类型异常处理器，就注册异常处理器
     *
     * @param exceptionFullName 处理的异常名称
     * @param handler           异常处理器
     *
     * @see RestExceptionEnum
     */
    public final static void registryIfAbsent(String exceptionFullName, RestExceptionHandler handler) {
        CACHE.putIfAbsent(exceptionFullName, handler);
    }

    /**
     * 查找异常处理器
     *
     * @param throwable 异常
     *
     * @return 异常处理器
     */
    public final static RestExceptionHandler findRestExceptionHandler(Throwable throwable) {
        return findRestExceptionHandler(throwable.getClass());
    }

    /**
     * 查找异常处理器
     *
     * @param exceptionType 异常类型
     *
     * @return 异常处理器
     */
    public final static RestExceptionHandler findRestExceptionHandler(Class exceptionType) {
        return findRestExceptionHandler(exceptionType.getName());
    }

    /**
     * 查找异常处理器
     *
     * @param exceptionName 异常名称
     *
     * @return 异常处理器
     */
    public final static RestExceptionHandler findRestExceptionHandler(String exceptionName) {
        return CACHE.get(exceptionName);
    }
}
