package com.moon.core.util.console;

import com.moon.core.util.Appender;
import com.moon.core.util.console.core.HtmlAppender;
import com.moon.core.util.console.core.TextAppender;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
final class AppenderUtil {
    private AppenderUtil() {
        noInstanceError();
    }

    private final static Map<ConsoleSettings, Appender> APPENDER_MAP;

    static {
        (APPENDER_MAP = new ConcurrentHashMap<>()).put(ConsoleDefault.SETTINGS, Appender.SYSTEM);
    }

    public final static Appender buildBySettings(ConsoleSettings consoleSettings) {
        return APPENDER_MAP.computeIfAbsent(consoleSettings, settings ->
            settings.isHtml() ? new HtmlAppender(settings) : new TextAppender(settings));
    }
}
