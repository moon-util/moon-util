package com.moon.processor.utils;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;

import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;

/**
 * @author benshaoye
 */
public enum Log2 {
    ;
    private static Messager messager;

    public static void initialize(ProcessingEnvironment env) {
        Log2.messager = env.getMessager();
    }

    public static void warning(String message, Object... values) {
        messager.printMessage(MANDATORY_WARNING, String2.format(message, values));
    }
}
