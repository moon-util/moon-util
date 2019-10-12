package com.moon.core.util.console;

import com.moon.core.enums.Const;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.Console;

/**
 * @author benshaoye
 */
class ConsoleDefault {
    private ConsoleDefault() {
        ThrowUtil.noInstanceError();
    }

    final static ConsoleSettings SETTINGS;

    public static ConsoleEnabled getEnabled() {
        return ConsoleGlobalControl.GLOBAL;
    }

    static {
        SETTINGS = new ConsoleSettings(
            false, true, Console.Level.PRINT, Const.EMPTY,
            new ConsoleControl.Classify[]{}, Const.EMPTY);
    }
}
