package com.moon.core.util.condition;

import com.moon.core.util.function.ThrowingRunnable;
import com.moon.core.util.function.ThrowingSupplier;

/**
 * 条件执行
 *
 * @author moonsky
 */
@FunctionalInterface
public interface Conditional {

    /**
     * 返回是否符合条件
     *
     * @return true: 符合条件
     */
    boolean isMatched();

    /**
     * 执行
     *
     * @param executor 符合条件时执行
     */
    default void runIfMatched(ThrowingRunnable executor) {
        if (isMatched()) {
            executor.uncheckedRun();
        }
    }

    /**
     * 不符合条件时执行
     *
     * @param executor 不符合条件时执行
     */
    default void runIfUnmatched(ThrowingRunnable executor) {
        if (!isMatched()) {
            executor.uncheckedRun();
        }
    }

    /**
     * 得到一个值，null 值或异常时返回 defaultVal
     *
     * @param supplier     符合条件时执行并返回结果
     * @param defaultValue 不符合条件时返回默认值
     * @param <T>          返回值类型
     *
     * @return T 类型的值
     */
    default <T> T getOrDefault(ThrowingSupplier<T> supplier, T defaultValue) {
        return isMatched() ? supplier.uncheckedGet() : defaultValue;
    }

    /**
     * 得到一个值，null 值或异常时返回 defaultSupplier 的值
     *
     * @param supplier        符合条件时执行并返回结果
     * @param defaultSupplier 不符合条件时执行降级策略
     * @param <T>             返回值类型
     *
     * @return T 类型的值
     */
    default <T> T getOrElse(ThrowingSupplier<T> supplier, ThrowingSupplier<T> defaultSupplier) {
        return isMatched() ? supplier.uncheckedGet() : defaultSupplier.uncheckedGet();
    }

    /**
     * 得到一个值，null 值或异常时返回 null
     *
     * @param supplier 符合条件时执行并返回结果
     * @param <T>      返回结果类型
     *
     * @return T 类型的值
     */
    default <T> T getOrNull(ThrowingSupplier<T> supplier) { return isMatched() ? supplier.uncheckedGet() : null; }
}
