package com.moon.mapping.processing;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;

import static com.moon.mapping.processing.EnvUtils.getMessager;
import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;

/**
 * @author benshaoye
 */
abstract class Logger {

    private Logger() {}

    private final static LongAdder ADDER = new LongAdder();

    static void warn(Messager messager, Object obj) {
        int idx = ADDER.intValue();
        ADDER.increment();
        StringBuilder builder = new StringBuilder().append(">> ").append(idx);
        builder.append(" : ").append(obj == null ? null : obj.toString());
        messager.printMessage(Diagnostic.Kind.WARNING, builder);
    }

    static void warn(Messager messager, String template, Object... values) {
        String formatted = StringUtils.format(true, template, values);
        warn(messager, String.valueOf(formatted));
    }

    static void warn(Object obj) {
        Messager messager = getMessager();
        if (messager != null) {
            warn(messager, obj);
        }
    }

    static void warn(String template, Object... values) {
        Messager messager = getMessager();
        if (messager != null) {
            warn(messager, template, values);
        }
    }

    static void printWarn(String template, Object...values){
        Logger.onLevel(MANDATORY_WARNING, () -> StringUtils.format(true, template, values));
    }

    static void onLevel(Diagnostic.Kind level, Supplier<String> content) {
        String message = content.get();
        if (message != null) {
            getMessager().printMessage(level, message);
        }
    }
}
