package com.moon.core.lang;

import com.moon.core.exception.DefaultException;
import com.moon.core.util.function.ThrowingConsumer;
import com.moon.core.util.function.ThrowingFunction;
import com.moon.core.util.function.ThrowingRunnable;

import java.util.function.Consumer;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public final class ThrowUtil {

    private ThrowUtil() {
        noInstanceError();
    }

    public static <T> T doThrow() {
        throw new IllegalArgumentException();
    }

    public static <T> T doThrow(String reason) {
        throw new IllegalArgumentException(reason);
    }
    public static <T> T doThrow(Object reason) {
        if (reason == null) {
            throw new NullPointerException();
        } else if (reason instanceof RuntimeException) {
            throw (RuntimeException) reason;
        } else if (reason instanceof Error) {
            throw (Error) reason;
        } else {
            return doThrow(String.valueOf(reason));
        }
    }

    public static <T> T wrapRuntime(Throwable e) {
        return doThrow(e);
    }

    public static <T> T wrapRuntime(Throwable e, String msg) {
        throw new DefaultException(msg, e);
    }

    /*
     * -----------------------------------------------------------------------
     * ignore exception
     * -----------------------------------------------------------------------
     */

    /**
     * 忽略指定类型异常
     *
     * @param t
     * @param type
     * @param <T>
     * @param <EX>
     * @return
     */
    public final static <T, EX extends Throwable> T ignoreAssignException(Throwable t, Class<EX> type) {
        if (type.isInstance(t)) {
            return null;
        }
        return doThrow(t);
    }

    /**
     * 忽略包含指定类型 cause 的异常，否则抛出异常
     *
     * @param t
     * @param type
     * @param <T>
     * @param <EX>
     * @return
     */
    public final static <T, EX extends Throwable> T ignoreAssignExceptionWithCause(Throwable t, Class<EX> type) {
        for (Throwable ex = t; ex != null; ex = ex.getCause()) {
            if (type.isInstance(ex)) {
                return null;
            }
        }
        return doThrow(t);
    }

    /**
     * 忽略指定类抛出的异常
     *
     * @param t
     * @param targetClass
     * @param <T>
     * @return
     */
    public final static <T> T ignoreAssignPosition(Throwable t, Class targetClass) {
        String name = targetClass.getName();
        for (Throwable th = t; th != null; th = th.getCause()) {
            StackTraceElement[] elements = th.getStackTrace();
            for (int i = 0; i < elements.length; i++) {
                if (name.equals(elements[i].getClassName())) {
                    return null;
                }
            }
        }
        return doThrow(t);
    }

    /**
     * 忽略自身或 cause 中包含指定方法抛出的异常，否则抛出异常
     *
     * @param t
     * @param targetClass
     * @param methodName
     * @param <T>
     * @return
     */
    public final static <T> T ignoreAssignPosition(Throwable t, Class targetClass, String methodName) {
        final String name = targetClass.getName();
        StackTraceElement element;
        for (Throwable th = t; th != null; th = th.getCause()) {
            StackTraceElement[] elements = th.getStackTrace();
            for (int i = 0; i < elements.length; i++) {
                if (name.equals((element = elements[i]).getClassName())
                    && methodName.equals(element.getMethodName())) {
                    return null;
                }
            }
        }
        return doThrow(t);
    }

    /*
     * -----------------------------------------------------------------------
     * ignore running
     * -----------------------------------------------------------------------
     */

    /**
     * 忽略所有异常调用、运行一段代码
     *
     * @param runner
     */
    public static void ignoreThrowsRun(ThrowingRunnable runner) {
        ignoreThrowsRun(runner, false);
    }

    /**
     * 如存在异常，打印异常信息，但不影响执行
     *
     * @param runner
     * @param println
     */
    public static void ignoreThrowsRun(ThrowingRunnable runner, boolean println) {
        optionalThrowsRun(runner, println ? Throwable::printStackTrace : t -> {});
    }

    public static void optionalThrowsRun(ThrowingRunnable runner, Consumer<Throwable> consumer) {
        try {
            runner.run();
        } catch (Throwable t) {
            consumer.accept(t);
        }
    }

    public static <T> void ignoreThrowsAccept(T value, ThrowingConsumer<T> consumer) {
        ignoreThrowsAccept(value, consumer, false);
    }

    public static <T> void ignoreThrowsAccept(T value, ThrowingConsumer<T> consumer, boolean println) {
        optionalThrowsAccept(value, consumer, println ? Throwable::printStackTrace : t -> {});
    }

    public static <T> void optionalThrowsAccept(T value, ThrowingConsumer<T> consumer, Consumer<Throwable> throwableConsumer) {
        try {
            consumer.accept(value);
        } catch (Throwable t) {
            throwableConsumer.accept(t);
        }
    }

    public static <T, R> R ignoreThrowsApply(T value, ThrowingFunction<T, R> function) {
        return ignoreThrowsApply(value, function, false);
    }

    public static <T, R> R ignoreThrowsApply(T value, ThrowingFunction<T, R> function, boolean println) {
        return optionalThrowsApply(value, function, println ? Throwable::printStackTrace : t -> {});
    }

    public static <T, R> R optionalThrowsApply(T value, ThrowingFunction<T, R> function, Consumer<Throwable> consumer) {
        try {
            return function.apply(value);
        } catch (Throwable t) {
            consumer.accept(t);
            return null;
        }
    }

    /*
     * -----------------------------------------------------------------------
     * reject access
     * -----------------------------------------------------------------------
     */

    /**
     * 用于私有构造方法等，抛出一个错误表示该类不能有实例存在
     *
     * @see java.util.Objects
     */
    public static void noInstanceError() {
        noInstanceError("No " + StackTraceUtil.getPrevCallerTypeName() + " instances for you!");
    }

    /**
     * 用于私有构造方法等，抛出一个错误表示该类不能有实例存在
     */
    public static void noInstanceError(String message) {
        throw new AssertionError(message);
    }

    /**
     * 不能访问指定位置
     */
    public final static <T> T rejectAccessError() {
        return rejectAccessError("Refuse to apply. \n\tLocation: " + StackTraceUtil.getPrevTraceOfSteps(1));
    }

    /***
     * 不能访问指定位置
     * @param message
     */
    public final static <T> T rejectAccessError(String message) {
        throw new IllegalAccessError(message);
    }

    public static <T> T unsupported(String message) { throw new UnsupportedOperationException(message); }

    public static <T> T unsupported(Object message) { return unsupported(String.valueOf(message)); }
}
