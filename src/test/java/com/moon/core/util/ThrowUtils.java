package com.moon.core.util;

import com.moon.core.exception.DefaultException;
import com.moon.core.lang.StackTraceUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.function.ThrowingConsumer;
import com.moon.core.util.function.ThrowingFunction;
import com.moon.core.util.function.ThrowingRunnable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
public class ThrowUtils {


    private ThrowUtils() { noInstanceError(); }

    public static <T> T illegalState() { throw new IllegalStateException(); }

    public static <T> T illegalState(String reason) { throw new IllegalStateException(reason); }

    public static <T> T illegalArg() { throw new IllegalArgumentException(); }

    public static <T> T illegalArg(String reason) { throw new IllegalArgumentException(reason); }

    public static <T> T runtime() { return illegalArg(); }

    public static <T> T runtime(String reason) { return illegalArg(reason); }

    public static <T> T runtime(Object reason) {
        if (reason == null) {
            throw new NullPointerException();
        } else if (reason instanceof Throwable) {
            return unchecked((Throwable) reason);
        } else if (reason instanceof Supplier) {
            return runtime(((Supplier) reason).get());
        } else {
            return runtime(String.valueOf(reason));
        }
    }

    public static <T> T unchecked(Throwable e) { return runtime(e); }

    public static <T> T unchecked(Throwable e, String msg) { return runtime(e, msg); }

    public static <T> T runtime(Throwable e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else if (e instanceof Error) {
            throw (Error) e;
        } else {
            throw new DefaultException(e);
        }
    }

    public static <T> T runtime(Throwable e, String msg) { throw new DefaultException(msg, e); }

    /*
     * -----------------------------------------------------------------------
     * ignore exception
     * -----------------------------------------------------------------------
     */

    /**
     * 忽略指定类型异常
     *
     * @param t    异常
     * @param type 异常类型
     * @param <T>  泛型（无意义）
     * @param <EX> 期望忽略的异常类型
     *
     * @return 如果被忽略就返回 null，否则抛出异常
     */
    private final static <T, EX extends Throwable> T ignoreAssignException(Throwable t, Class<EX> type) {
        if (type.isInstance(t)) {
            return null;
        }
        return ThrowUtil.runtime(t);
    }

    /**
     * 忽略包含指定类型 cause 的异常，否则抛出异常
     *
     * @param t    异常
     * @param type 异常类型
     * @param <T>  泛型（无意义）
     * @param <EX> 期望忽略的异常类型
     *
     * @return 如果被忽略就返回 null，否则抛出异常
     */
    private final static <T, EX extends Throwable> T ignoreAssignExceptionWithCause(Throwable t, Class<EX> type) {
        for (Throwable ex = t; ex != null; ex = ex.getCause()) {
            if (type.isInstance(ex)) {
                return null;
            }
        }
        return ThrowUtil.runtime(t);
    }

    /**
     * 忽略指定类抛出的异常
     *
     * @param t           异常
     * @param targetClass 目标位置
     * @param <T>         泛型（无意义）
     *
     * @return 如果被忽略就返回 null，否则抛出异常
     */
    private final static <T> T ignoreAssignPosition(Throwable t, Class targetClass) {
        String name = targetClass.getName();
        for (Throwable th = t; th != null; th = th.getCause()) {
            StackTraceElement[] elements = th.getStackTrace();
            for (int i = 0; i < elements.length; i++) {
                if (name.equals(elements[i].getClassName())) {
                    return null;
                }
            }
        }
        return ThrowUtil.runtime(t);
    }

    /**
     * 忽略自身或 cause 中包含指定方法抛出的异常，否则抛出异常
     *
     * @param t           异常
     * @param targetClass 目标位置
     * @param methodName  目标方法
     * @param <T>         异常泛型
     *
     * @return 如果被忽略就返回 null，否则抛出异常
     */
    private final static <T> T ignoreAssignPosition(Throwable t, Class targetClass, String methodName) {
        final String name = targetClass.getName();
        StackTraceElement element;
        for (Throwable th = t; th != null; th = th.getCause()) {
            StackTraceElement[] elements = th.getStackTrace();
            for (int i = 0; i < elements.length; i++) {
                if (name.equals((element = elements[i]).getClassName()) && methodName.equals(element.getMethodName())) {
                    return null;
                }
            }
        }
        return ThrowUtil.runtime(t);
    }

    /*
     * -----------------------------------------------------------------------
     * ignore running
     * -----------------------------------------------------------------------
     */

    /**
     * 忽略所有异常调用、运行一段代码
     *
     * @param runner 将要执行的代码块
     */
    public static void ignoreThrowsRun(ThrowingRunnable runner) {
        ignoreThrowsRun(runner, false);
    }

    /**
     * 如存在异常，打印异常信息，但不影响执行
     *
     * @param runner  将要执行的代码块
     * @param println 是否打印异常栈
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

    public static <T> void optionalThrowsAccept(
        T value, ThrowingConsumer<T> consumer, Consumer<Throwable> throwableConsumer
    ) {
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

    /**
     * 不能访问指定位置
     *
     * @param message 异常信息
     */
    public final static <T> T rejectAccessError(String message) {
        throw new IllegalAccessError(message);
    }

    public static <T> T unsupported() { throw new UnsupportedOperationException(); }

    public static <T> T unsupported(String message) { throw new UnsupportedOperationException(message); }

    public static <T> T unsupported(Object reason) { return unsupported(String.valueOf(reason)); }
}
