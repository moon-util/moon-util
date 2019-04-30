package com.moon.core.util.console;

import static com.moon.core.util.Console.Level;

/**
 * @author benshaoye
 */
interface ConsoleEnabled {
    /**
     * 设置最低输出级别
     *
     * @param lowestLevel
     * @return 返回是否设置成功
     */
    boolean setLowestLevel(Level lowestLevel);

    /**
     * 返回最多输出级别
     *
     * @return
     */
    Level getLowestLevel();

    /**
     * 设置是否允许控制台输出
     *
     * @param allowSystemOut
     * @return
     */
    ConsoleEnabled setAllowSystemOut(boolean allowSystemOut);

    /**
     * 是否允许控制台输出
     *
     * @return
     */
    boolean isAllowSystemOut();

    /**
     * 是否允许指定级别控制
     *
     * @param level
     * @return
     */
    boolean isEnabled(Level level);

    /**
     * 是否允许 PRINT 级别控制
     *
     * @return
     */
    default boolean isPrintEnabled() {
        return isEnabled(Level.PRINT);
    }

    /**
     * 是否允许 DEBUG 级别控制
     *
     * @return
     */
    default boolean isDebugEnabled() {
        return isEnabled(Level.DEBUG);
    }

    /**
     * 是否允许 INFO 级别控制
     *
     * @return
     */
    default boolean isInfoEnabled() {
        return isEnabled(Level.INFO);
    }

    /**
     * 是否允许 WARN 级别控制
     *
     * @return
     */
    default boolean isWarnEnabled() {
        return isEnabled(Level.WARN);
    }

    /**
     * 是否允许 ERROR 级别控制
     *
     * @return
     */
    default boolean isErrorEnabled() {
        return isEnabled(Level.ERROR);
    }

    /**
     * 是否允许 ASSERT 级别控制
     *
     * @return
     */
    default boolean isAssertEnabled() {
        return isEnabled(Level.ASSERT);
    }
}
