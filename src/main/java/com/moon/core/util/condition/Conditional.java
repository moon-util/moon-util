package com.moon.core.util.condition;

import com.moon.core.lang.ref.LazyAccessor;
import com.moon.core.util.function.ThrowingRunnable;
import com.moon.core.util.function.ThrowingSupplier;

import java.util.function.BooleanSupplier;

/**
 * 条件执行
 *
 * @author moonsky
 */
@FunctionalInterface
public interface Conditional extends BooleanSupplier {

    /**
     * 返回固定条件执行器
     *
     * @param matched 是否符合条件
     *
     * @return 固定条件条件执行
     */
    static Conditional of(boolean matched) {
        return FinallyCondition.of(matched);
    }

    /**
     * 延迟加载的固定条件执行器
     *
     * @param accessor 是否符合条件
     *
     * @return 延迟固定条件条件执行
     */
    static Conditional of(LazyAccessor<Boolean> accessor) {
        return FinallyCondition.of(accessor);
    }

    /**
     * 返回动态条件执行器
     *
     * @param dynamicCondition 动态条件
     *
     * @return 动态条件执行器
     */
    static Conditional ofDynamic(BooleanSupplier dynamicCondition) {
        return DynamicCondition.of(dynamicCondition);
    }

    /**
     * 返回是否符合条件
     *
     * @return true: 符合条件
     */
    boolean isMatched();

    /**
     * 返回是否符合条件
     *
     * @return true: 符合条件
     */
    @Override
    default boolean getAsBoolean() { return isMatched(); }

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
