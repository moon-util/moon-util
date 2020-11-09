package com.moon.mapping.processing;

/**
 * @author benshaoye
 */
@FunctionalInterface
interface BiIntFunction<F, S, R> {

    /**
     * 执行函数
     *
     * @param first  第一个参数
     * @param second 第二个参数
     * @param value  第三个参数
     *
     * @return 返回值
     */
    R apply(F first, S second, int value);
}
