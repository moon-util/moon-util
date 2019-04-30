package com.moon.core.util.console;

import com.moon.core.util.Console.Level;

/**
 * @author benshaoye
 */
interface IPrintable {
    /**
     * 换行
     *
     * @param level level
     */
    void println(Level level);

    /**
     * 输出指定级别信息
     *
     * @param level level
     * @param val   值
     */
    void println(Level level, Object val);

    /**
     * 输出指定级别信息
     *
     * @param level    level
     * @param template 模板，‘{}’占位符
     * @param value    value
     */
    void println(Level level, String template, Object value);

    /**
     * 输出指定级别信息
     *
     * @param level    level
     * @param template 模板，‘{}’占位符
     * @param value1   the first value
     * @param value2   the second value
     */
    void println(Level level, String template, Object value1, Object value2);

    /**
     * 输出指定级别信息
     *
     * @param level    level
     * @param template 模板，‘{}’占位符
     * @param value1   the first value
     * @param value2   the second value
     * @param value3   the third value
     */
    void println(Level level, String template, Object value1, Object value2, Object value3);

    /**
     * 输出指定级别信息
     *
     * @param level    level
     * @param template 模板，‘{}’占位符
     * @param values   values
     */
    void println(Level level, String template, Object... values);

    /**
     * 输出指定级别信息
     *
     * @param level  level
     * @param values values
     */
    void println(Level level, Object... values);
}
