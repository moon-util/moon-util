package com.moon.core.lang;

import com.moon.core.io.IOUtil;
import com.moon.core.util.OptionalUtil;
import com.moon.core.util.function.*;

import java.io.Closeable;

import static com.moon.core.lang.ThrowUtil.doThrow;
import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class LangUtil {
    private LangUtil() {
        noInstanceError();
    }

    /**
     * 忽略检查异常执行
     *
     * @param run
     */
    public static void run(ThrowsRunnable run) {
        try {
            run.run();
        } catch (Throwable t) {
            doThrow(t);
        }
    }

    /**
     * 忽略检查异常获取一个值
     *
     * @param supplier
     * @param <T>
     * @return
     */
    public static <T> T get(ThrowsSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            return doThrow(t);
        }
    }

    public static <T> T getOrDefault(ThrowsSupplier<T> supplier, T defaultValue) {
        try {
            return OptionalUtil.getOrDefault(supplier.get(), defaultValue);
        } catch (Throwable t) {
            return defaultValue;
        }
    }

    public static <T> T defaultWhenThrow(ThrowsSupplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            return defaultValue;
        }
    }

    /**
     * 忽略检查异常消费
     *
     * @param value
     * @param consumer
     * @param <T>
     */
    public static <T> void accept(T value, ThrowsConsumer<? super T> consumer) {
        try {
            consumer.accept(value);
        } catch (Throwable t) {
            doThrow(t);
        }
    }

    /**
     * 忽略检查异常转换
     *
     * @param value
     * @param function
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> R apply(T value, ThrowsFunction<? super T, R> function) {
        try {
            return function.apply(value);
        } catch (Throwable t) {
            return doThrow(t);
        }
    }

    /**
     * 忽略检查异常消费
     *
     * @param v1
     * @param v2
     * @param consumer
     * @param <T>
     * @param <O>
     */
    public static <T, O> void acceptBi(T v1, O v2, ThrowsBiConsumer<? super T, ? super O> consumer) {
        try {
            consumer.accept(v1, v2);
        } catch (Throwable t) {
            doThrow(t);
        }
    }

    /**
     * 忽略检查异常转换
     *
     * @param v1
     * @param v2
     * @param function
     * @param <T>
     * @param <O>
     * @param <R>
     * @return
     */
    public static <T, O, R> R applyBi(T v1, O v2, ThrowsBiFunction<? super T, ? super O, R> function) {
        try {
            return function.apply(v1, v2);
        } catch (Throwable t) {
            return doThrow(t);
        }
    }
}
