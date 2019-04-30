package com.moon.core.util.console;

import com.moon.core.util.Console;

/**
 * @author benshaoye
 */
interface IWarnPrintable extends IPrintable {
    /**
     * 换行
     */
    default void warn() {
        this.println(Console.Level.WARN);
    }

    /**
     * 输出 WARN 级别信息
     *
     * @param val 值
     */
    default void warn(Object val) {
        this.println(Console.Level.WARN, val);
    }

    /**
     * 输出 WARN 级别信息
     *
     * @param template 模板，‘{}’占位符
     * @param value    value
     */
    default void warn(String template, Object value) {
        this.println(Console.Level.WARN, template, value);
    }

    /**
     * 输出 WARN 级别信息
     *
     * @param template 模板，‘{}’占位符
     * @param value1   the first value
     * @param value2   the second value
     */
    default void warn(String template, Object value1, Object value2) {
        this.println(Console.Level.WARN, template, value1, value2);
    }

    /**
     * 输出 WARN 级别信息
     *
     * @param template 模板，‘{}’占位符
     * @param value1   the first value
     * @param value2   the second value
     * @param value3   the third value
     */
    default void warn(String template, Object value1, Object value2, Object value3) {
        this.println(Console.Level.WARN, template, value1, value2, value3);
    }

    /**
     * 输出 WARN 级别信息
     *
     * @param template 模板，‘{}’占位符
     * @param values   values
     */
    default void warn(String template, Object... values) {
        this.println(Console.Level.WARN, template, values);
    }

    /**
     * 输出 WARN 级别信息
     *
     * @param values values
     */
    default void warn(Object... values) {
        this.println(Console.Level.WARN, values);
    }
}
