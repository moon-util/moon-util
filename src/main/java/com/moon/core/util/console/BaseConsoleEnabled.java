package com.moon.core.util.console;

import com.moon.core.util.Console;
import com.moon.core.util.Console.Level;

import static com.moon.core.util.console.ConsoleDefault.getEnabled;

/**
 * @author benshaoye
 */
abstract class BaseConsoleEnabled implements ConsoleEnabled {

    private boolean allowSystemOut = true;
    private Level lowestLevel = Console.LOWEST;

    /**
     * 设置最低输出级别
     *
     * @param lowestLevel
     */
    @Override
    public synchronized boolean setLowestLevel(Level lowestLevel) {
        this.lowestLevel = lowestLevel;
        return true;
    }

    @Override
    public final Level getLowestLevel() {
        return lowestLevel;
    }

    @Override
    public final synchronized BaseConsoleEnabled setAllowSystemOut(boolean allowSystemOut) {
        this.allowSystemOut = allowSystemOut;
        return this;
    }

    @Override
    public final boolean isAllowSystemOut() {
        return getEnabled().isAllowSystemOut() && allowSystemOut;
    }

    /**
     * 是否允许指定级别控制
     *
     * @param level
     * @return
     */
    @Override
    public final boolean isEnabled(Level level) {
        return getEnabled().isEnabled(level) && level.ordinal() <= this.lowestLevel.ordinal();
    }
}
