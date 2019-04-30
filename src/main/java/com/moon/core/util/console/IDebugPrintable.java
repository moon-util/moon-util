package com.moon.core.util.console;

import com.moon.core.util.Console;

/**
 * @author benshaoye
 */
interface IDebugPrintable extends IPrintable {
    /**
     * 换行
     */
    default void debug() {
        this.println(Console.Level.DEBUG);
    }

    /**
     * 输出 DEBUG 级别信息
     *
     * @param val 值
     */
    default void debug(Object val) {
        this.println(Console.Level.DEBUG, val);
    }

    /**
     * 输出 DEBUG 级别信息
     *
     * @param template 模板，‘{}’占位符
     * @param value    value
     */
    default void debug(String template, Object value) {
        this.println(Console.Level.DEBUG, template, value);
    }

    /**
     * 输出 DEBUG 级别信息
     *
     * @param template 模板，‘{}’占位符
     * @param value1   the first value
     * @param value2   the second value
     */
    default void debug(String template, Object value1, Object value2) {
        this.println(Console.Level.DEBUG, template, value1, value2);
    }

    /**
     * 输出 DEBUG 级别信息
     *
     * @param template 模板，‘{}’占位符
     * @param value1   the first value
     * @param value2   the second value
     * @param value3   the third value
     */
    default void debug(String template, Object value1, Object value2, Object value3) {
        this.println(Console.Level.DEBUG, template, value1, value2, value3);
    }

    /**
     * 输出 DEBUG 级别信息
     *
     * @param template 模板，‘{}’占位符
     * @param values   values
     */
    default void debug(String template, Object... values) {
        this.println(Console.Level.DEBUG, template, values);
    }

    /**
     * 输出 DEBUG 级别信息
     *
     * @param values values
     */
    default void debug(Object... values) {
        this.println(Console.Level.DEBUG, values);
    }
}
