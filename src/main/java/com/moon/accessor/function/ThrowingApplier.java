package com.moon.accessor.function;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface ThrowingApplier<T, R> {

    /**
     * 执行函数
     *
     * @param param 参数
     *
     * @return 返回值
     *
     * @throws Throwable 任意异常
     */
    R apply(T param) throws Throwable;
}
