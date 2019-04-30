package com.moon.core.util.console;


import static com.moon.core.util.Console.Level.PRINT;

/**
 * @author benshaoye
 */
interface IPrintlnPrintable extends IPrintable {
    /**
     * 打印异常信息
     *
     * @param t 异常对象
     */
    void println(Throwable t);

    /**
     * 换行
     */
    default void println() {
        this.println(PRINT);
    }

    /**
     * 输出 PRINT 级别信息
     *
     * @param val 值
     */
    default void println(Object val) {
        this.println(PRINT, val);
    }

    /**
     * 输出 PRINT 级别信息
     *
     * @param template 模板，‘{}’占位符
     * @param value    value
     */
    default void println(String template, Object value) {
        this.println(PRINT, template, value);
    }

    /**
     * 输出 PRINT 级别信息
     *
     * @param template 模板，‘{}’占位符
     * @param value1   the first value
     * @param value2   the second value
     */
    default void println(String template, Object value1, Object value2) {
        this.println(PRINT, template, value1, value2);
    }

    /**
     * 输出 PRINT 级别信息
     *
     * @param template 模板，‘{}’占位符
     * @param value1   the first value
     * @param value2   the second value
     * @param value3   the third value
     */
    default void println(String template, Object value1, Object value2, Object value3) {
        this.println(PRINT, template, value1, value2, value3);
    }

    /**
     * 输出 PRINT 级别信息
     *
     * @param template 模板，‘{}’占位符
     * @param values   values
     */
    default void println(String template, Object... values) {
        this.println(PRINT, template, values);
    }

    /**
     * 输出 PRINT 级别信息
     *
     * @param values values
     */
    default void println(Object... values) {
        this.println(PRINT, values);
    }
}
