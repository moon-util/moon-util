package com.moon.core.util.console.core;

import com.moon.core.util.Appender;
import com.moon.core.util.Console;
import com.moon.core.util.console.ConsoleSettings;

import java.io.File;
import java.io.PrintStream;

/**
 * @author benshaoye
 */
public class HtmlAppender
    extends BaseAppender
    implements Appender {

    public HtmlAppender(ConsoleSettings settings) {
        super(settings);
    }

    /**
     * 获取输出对象
     *
     * @return
     */
    @Override
    public PrintStream apply(Console.Level level) {
        File base = getBasePath();
        if (base == null) {
            return System.out;
        } else {
            return System.out;
        }
    }
}
