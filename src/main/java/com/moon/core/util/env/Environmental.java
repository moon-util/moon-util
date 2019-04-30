package com.moon.core.util.env;

import com.moon.core.util.function.ThrowsRunnable;
import com.moon.core.util.function.ThrowsSupplier;

/**
 * @author benshaoye
 */
public interface Environmental {
    /**
     * 执行
     *
     * @param executor
     */
    void run(ThrowsRunnable executor);

    /**
     * 得到一个值，null 值或异常时返回 defaultVal
     *
     * @param supplier
     * @param defaultValue
     * @param <T>
     * @return
     */
    <T> T getOrDefault(ThrowsSupplier<T> supplier, T defaultValue);

    /**
     * 得到一个值，null 值或异常时返回 defaultSupplier 的值
     *
     * @param supplier
     * @param defaultSupplier
     * @param <T>
     * @return
     */
    <T> T getOrElse(ThrowsSupplier<T> supplier, ThrowsSupplier<T> defaultSupplier);

    /**
     * 得到一个值，null 值或异常时返回 null
     *
     * @param supplier
     * @param <T>
     * @return
     */
    <T> T getOrNull(ThrowsSupplier<T> supplier);
}
