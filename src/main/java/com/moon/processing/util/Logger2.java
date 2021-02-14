package com.moon.processing.util;

import com.moon.processor.utils.String2;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.util.concurrent.atomic.AtomicInteger;

import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * @author benshaoye
 */
public enum Logger2 {
    ;
    private final static AtomicInteger INDEXER = new AtomicInteger();

    private static Messager getMessager() { return Processing2.getMessager(); }

    private static void messageOf(Diagnostic.Kind kind, String message) {
        getMessager().printMessage(kind, ">> " + INDEXER.getAndIncrement() + " " + message);
    }

    public static void warn(Object message) { messageOf(WARNING, String.valueOf(message)); }

    public static void warn(String message, Object... values) {
        messageOf(WARNING, String2.format(message, values)); }

    public static void println(String message, Object... values) {
        messageOf(MANDATORY_WARNING, String2.format(message, values));
    }
}
