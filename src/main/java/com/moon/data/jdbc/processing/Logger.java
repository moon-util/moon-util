package com.moon.data.jdbc.processing;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author benshaoye
 */
abstract class Logger {

    private static Messager MESSAGER;

    private final static LongAdder ADDER = new LongAdder();

    private Logger() {}

    static void initialize(Messager messager) { MESSAGER = messager; }

    static void warn(Object obj) {
        int idx = ADDER.intValue();
        ADDER.increment();
        StringBuilder builder = new StringBuilder().append(">> ").append(idx);
        builder.append(" : ").append(obj == null ? null : obj.toString());
        MESSAGER.printMessage(Diagnostic.Kind.WARNING, builder);
    }

    static void warn(String template, Object... values) {
        String formatted = StringUtils.format(true, template, values);
        warn(String.valueOf(formatted));
    }
}
