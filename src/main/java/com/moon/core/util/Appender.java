package com.moon.core.util;

import com.moon.core.util.concurrent.ExecutorUtil;

import java.io.PrintStream;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface Appender extends Function<Console.Level, PrintStream> {

    Appender SYSTEM = level -> System.out;

    /**
     * 获取指定级别输出对象
     *
     * @param level
     * @return
     */
    @Override
    PrintStream apply(Console.Level level);

    /**
     * 追加输出指定级别内容
     *
     * @param level
     * @param supplier
     */
    default void append(Console.Level level, Supplier supplier) {
        ExecutorUtil.execute(() -> apply(level).println(supplier.get()));
    }
}
