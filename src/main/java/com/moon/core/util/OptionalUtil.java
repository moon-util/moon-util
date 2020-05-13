package com.moon.core.util;

import com.moon.core.lang.Executable;
import com.moon.core.lang.ThrowUtil;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.*;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
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
        return obj == null ? ThrowUtil.runtime() : function.apply(obj);
    }

    public static <T, R> R computeOrThrow(T obj, Function<T, R> function, String message) {
        return obj == null ? ThrowUtil.runtime(message) : function.apply(obj);
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
            ThrowUtil.runtime();
        } else {
            consumer.accept(obj);
        }
    }

    public static <T> void ifPresentOrThrow(T obj, Consumer<T> consumer, String message) {
        if (obj == null) {
            ThrowUtil.runtime(message);
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

    public static <T> T getOrElse(T value, Supplier<T> defaultSupplier) {
        return value == null ? defaultSupplier.get() : value;
    }

    public static Object resolveOrNull(Object optionalReference) {
        Object value = optionalReference;
        if (value instanceof java.util.Optional) {
            java.util.Optional optional = (java.util.Optional) value;
            value = optional.isPresent() ? optional.get() : null;
        } else if (value instanceof OptionalLong) {
            OptionalLong optional = (OptionalLong) value;
            value = optional.isPresent() ? optional.getAsLong() : null;
        } else if (value instanceof OptionalInt) {
            OptionalInt optional = (OptionalInt) value;
            value = optional.isPresent() ? optional.getAsInt() : null;
        } else if (value instanceof OptionalDouble) {
            OptionalDouble optional = (OptionalDouble) value;
            value = optional.isPresent() ? optional.getAsDouble() : null;
        } else if (value instanceof Optional) {
            value = ((Optional) value).getOrNull();
        }
        return value;
    }
}
