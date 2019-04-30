package com.moon.core.util;

import com.moon.core.lang.Executable;
import com.moon.core.lang.ThrowUtil;

import java.util.function.*;

import static com.moon.core.lang.ThrowUtil.doThrow;
import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 * @date 2018/9/14
 */
public final class OptionalUtil {
    private OptionalUtil() {
        noInstanceError();
    }

    /*
     * -----------------------------------------------------------
     * return int value
     * -----------------------------------------------------------
     */

    public static <T> int computeAsIntOrZero(T obj, ToIntFunction<T> function) {
        return computeAsIntOrDefault(obj, function, 0);
    }

    public static <T> int computeAsIntOrOne(T obj, ToIntFunction<T> function) {
        return computeAsIntOrDefault(obj, function, 1);
    }

    public static <T> int computeAsIntOrDefault(T obj, ToIntFunction<T> fn, int defaultValue) {
        return obj == null ? defaultValue : fn.applyAsInt(obj);
    }

    public static <T> int computeAsIntOrGet(T obj, ToIntFunction<T> function, IntSupplier supplier) {
        return obj == null ? supplier.getAsInt() : function.applyAsInt(obj);
    }

    /*
     * -----------------------------------------------------------
     * return value
     * -----------------------------------------------------------
     */

    public static <T, R> R computeOrNull(T obj, Function<T, R> function) {
        return computeOrDefault(obj, function, null);
    }

    public static <T, R> R computeOrDefault(T obj, Function<T, R> function, R elseVal) {
        return obj == null ? elseVal : function.apply(obj);
    }

    public static <T, R> R computeOrGet(T obj, Function<T, R> function, Supplier<R> supplier) {
        return obj == null ? supplier.get() : function.apply(obj);
    }

    public static <T, R> R computeOrThrow(T obj, Function<T, R> function) {
        return obj == null ? ThrowUtil.doThrow() : function.apply(obj);
    }

    public static <T, R> R computeOrThrow(T obj, Function<T, R> function, String message) {
        return obj == null ? doThrow(message) : function.apply(obj);
    }

    /*
     * -----------------------------------------------------------
     * doesn't has return value
     * -----------------------------------------------------------
     */

    public static <T> void ifPresent(T obj, Consumer<T> consumer) {
        if (obj != null) {
            consumer.accept(obj);
        }
    }

    public static <T, U> void ifPresent(T obj, BiConsumer<T, U> consumer, U param) {
        if (obj != null) {
            consumer.accept(obj, param);
        }
    }

    public static <T> void ifPresentOrElse(T obj, Consumer<T> consumer, Executable runnable) {
        if (obj == null) {
            runnable.execute();
        } else {
            consumer.accept(obj);
        }
    }

    public static <T> void ifPresentOrThrow(T obj, Consumer<T> consumer) {
        if (obj == null) {
            ThrowUtil.doThrow();
        } else {
            consumer.accept(obj);
        }
    }

    public static <T> void ifPresentOrThrow(T obj, Consumer<T> consumer, String message) {
        if (obj == null) {
            doThrow(message);
        } else {
            consumer.accept(obj);
        }
    }

    /*
     * getSheet or else
     */

    public static <T> T getOrDefault(T value, T defaultVal) {
        return value == null ? defaultVal : value;
    }

    public static <T> T elseGetIfNull(T value, Supplier<T> defaultSupplier) {
        return value == null ? defaultSupplier.get() : value;
    }
}
