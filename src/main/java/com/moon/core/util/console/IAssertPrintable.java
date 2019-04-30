package com.moon.core.util.console;

import com.moon.core.util.Console.Level;

/**
 * fatal
 * @author benshaoye
 */
interface IAssertPrintable extends IPrintable {
    /**
     * 换行
     */
    default void test() {
        this.println(Level.ASSERT);
    }

    /**
     * 输出 ASSERT 级别信息
     *
     * @param val 值
     */
    default void test(Object val) {
        this.println(Level.ASSERT, val);
    }

    /**
     * 输出 ASSERT 级别信息
     *
     * @param template 模板，‘{}’占位符
     * @param value    value
     */
    default void test(String template, Object value) {
        this.println(Level.ASSERT, template, value);
    }

    /**
     * 输出 ASSERT 级别信息
     *
     * @param template 模板，‘{}’占位符
     * @param value1   the first value
     * @param value2   the second value
     */
    default void test(String template, Object value1, Object value2) {
        this.println(Level.ASSERT, template, value1, value2);
    }

    /**
     * 输出 ASSERT 级别信息
     *
     * @param template 模板，‘{}’占位符
     * @param value1   the first value
     * @param value2   the second value
     * @param value3   the third value
     */
    default void test(String template, Object value1, Object value2, Object value3) {
        this.println(Level.ASSERT, template, value1, value2, value3);
    }

    /**
     * 输出 ASSERT 级别信息
     *
     * @param template 模板，‘{}’占位符
     * @param values   values
     */
    default void test(String template, Object... values) {
        this.println(Level.ASSERT, template, values);
    }

    /**
     * 输出 ASSERT 级别信息
     *
     * @param values values
     */
    default void test(Object... values) {
        this.println(Level.ASSERT, values);
    }
}
