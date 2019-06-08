package com.moon.core.util;

import com.moon.core.util.console.*;

import static com.moon.core.lang.EnumUtil.last;

/**
 * Appender 决定输出位置
 * Level 决定输出级别
 * <p>
 * {@link ConsoleControl}可以在初始化的时候决定一些基础配置 ：
 * 1、{@link #setLowestLevel(Level)}设置的 Level 不能低于{@link ConsoleControl#lowestLevel()}
 * 2、{@link ConsoleControl#async()}默认为 true，此时有一个单独的线程用于输出，否则由调用者直接输出
 * 3、{@link ConsoleControl}能设置输出位置，但是可以通过{@link #setAppender(Appender)}重新设置
 * 4、{@link ConsoleControl#async()}、{@link ConsoleControl#html()}、{@link ConsoleControl#pattern()}、
 * {@link ConsoleControl#lowestLevel()}一旦设定，便一直有效，不随重新设置{@link Appender}而改变
 * 5、输出分类：{@link ConsoleControl#classifies()}，按配置的先后顺序分类
 *
 * @author benshaoye
 */
public interface Console extends Printable, Timing {
    /**
     * 最低级别
     */
    Level LOWEST = Level.PRINT;
    /**
     * 全局默认输出
     */
    Console out = of();

    /**
     * 默认 Console 实例
     *
     * @return
     */
    static Console of() {
        return new GenericConsolePrinter();
    }

    /**
     * 自定义输出顶层类的 Console 实例
     *
     * @param topClass
     * @return
     */
    static Console of(Class topClass) {
        return new GenericConsolePrinter(topClass);
    }

    /**
     * 设置输出流
     *
     * @param appender
     * @return
     */
    Console setAppender(Appender appender);

    /**
     * 设置是否允许当前实例输出内容
     *
     * @param allowOutput
     * @see #isClosed()
     */
    void setAllowOutputStatus(boolean allowOutput);

    /**
     * 当前 Console 是否不可以输出
     *
     * @return 返回是否当前实例是否可以输出
     * @see #setAllowOutputStatus(boolean)
     */
    boolean isClosed();

    /**
     * 打印调用栈
     */
    default void printStackTrace() {
        printStackTrace(Level.DEBUG);
    }

    /**
     * 以指定输出级别打印堆栈追踪信息
     *
     * @param level
     */
    void printStackTrace(Level level);

    /**
     * 级别控制
     */
    enum Level {
        /**
         * 输出级别：最高，用于单元测试
         *
         * @see IAssertPrintable#test()
         * @see IPrintable#println(Level)
         */
        ASSERT,
        /**
         * 输出级别，输出错误信息
         *
         * @see IErrorPrintable#error()
         * @see IPrintable#println(Level)
         */
        ERROR,
        /**
         * 输出警告信息
         *
         * @see IWarnPrintable#warn()
         * @see IPrintable#println(Level)
         */
        WARN,
        /**
         * 输出常规信息
         *
         * @see IInfoPrintable#info()
         * @see IPrintable#println(Level)
         */
        INFO,
        /**
         * 输出调试信息
         *
         * @see IDebugPrintable#debug()
         * @see IPrintable#println(Level)
         */
        DEBUG,
        /**
         * 最低级别输出，即，输出所有信息
         *
         * @see IPrintlnPrintable#println()
         * @see IPrintable#println(Level)
         */
        PRINT
    }
}
