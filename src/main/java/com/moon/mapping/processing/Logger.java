package com.moon.mapping.processing;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author benshaoye
 */
abstract class Logger {

    private Logger() {}

    private static ProcessingEnvironment ENV;

    private final static LongAdder ADDER = new LongAdder();

    static void initialize(ProcessingEnvironment environment) {
        ENV = environment;
    }

    static void warn(ProcessingEnvironment env, Object obj) {
        warn(env.getMessager(), obj);
    }

    static void warn(Messager messager, Object obj) {
        int idx = ADDER.intValue();
        ADDER.increment();
        StringBuilder builder = new StringBuilder().append(">> ").append(idx);
        builder.append(" : ").append(obj == null ? null : obj.toString());
        messager.printMessage(Diagnostic.Kind.WARNING, builder);
    }

    static void warn(ProcessingEnvironment env, String template, Object... values) {
        warn(env.getMessager(), template, values);
    }

    static void warn(Messager messager, String template, Object... values) {
        if (values != null) {
            for (Object value : values) {
                template = template.replace("{}", value == null ? "null" : value.toString());
            }
            warn(messager, template);
        }
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

    private static Messager getMessager() {
        return ENV == null ? null : ENV.getMessager();
    }
}
