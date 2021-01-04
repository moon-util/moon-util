package com.moon.processor.utils;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import java.util.concurrent.atomic.AtomicInteger;

import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * @author benshaoye
 */
public enum Log2 {
    ;
    private static Messager messager;

    private final static AtomicInteger INDEXER = new AtomicInteger();

    private static String messageOf(String message) {
        return ">> " + INDEXER.getAndIncrement() + " " + message;
    }

    public static void initialize(ProcessingEnvironment env) { messager = env.getMessager(); }

    public static void warn(String message, Object... values) {
        messager.printMessage(WARNING, messageOf(String2.format(message, values)));
    }

    public static void warning(String message, Object... values) {
        messager.printMessage(MANDATORY_WARNING, String2.format(message, values));
    }
}
