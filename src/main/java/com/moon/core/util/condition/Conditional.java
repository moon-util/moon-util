package com.moon.core.util.condition;

import com.moon.core.lang.ref.LazyAccessor;
import com.moon.core.util.function.ThrowingRunnable;
import com.moon.core.util.function.ThrowingSupplier;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

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
    static Conditional of(boolean matched) { return DynamicCondition.of(matched); }

    /**
     * 返回动态条件执行器
     *
     * @param dynamicCondition 动态条件
     *
     * @return 动态条件执行器
     */
    static Conditional of(BooleanSupplier dynamicCondition) {
        return DynamicCondition.of(dynamicCondition);
    }

    /**
     * 返回是否符合条件
     *
     * @return true: 符合条件
     */
    boolean isTrue();

    /**
     * 是否不符合条件
     *
     * @return true: 不符合条件
     */
    default boolean isFalse() { return !isTrue(); }

    /**
     * 是否符合期望条件
     *
     * @param expected
     *
     * @return
     */
    default boolean isTrueAnd(boolean expected) { return expected && isTrue(); }

    /**
     * 是否不符合期望条件
     *
     * @param expected
     *
     * @return
     */
    default boolean isFalseAnd(boolean expected) { return expected && isFalse(); }

    /**
     * 返回是否符合条件
     *
     * @return true: 符合条件
     */
    @Override
    default boolean getAsBoolean() { return isTrue(); }

    /**
     * 执行
     *
     * @param executor 符合条件时执行
     */
    default void ifTrue(ThrowingRunnable executor) {
        if (isTrue()) {
            executor.uncheckedRun();
        }
    }

    /**
     * 不符合条件时执行
     *
     * @param executor 不符合条件时执行
     */
    default void ifFalse(ThrowingRunnable executor) {
        if (!isTrue()) {
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
        return isTrue() ? supplier.uncheckedGet() : defaultValue;
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
        return isTrue() ? supplier.uncheckedGet() : defaultSupplier.uncheckedGet();
    }

    /**
     * 得到一个值，null 值或异常时返回 null
     *
     * @param supplier 符合条件时执行并返回结果
     * @param <T>      返回结果类型
     *
     * @return T 类型的值
     */
    default <T> T getOrNull(ThrowingSupplier<T> supplier) { return getOrDefault(supplier, null); }
}
