package com.moon.processing.util;

import com.moon.processor.utils.Environment2;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author benshaoye
 */
public enum Processing2 {
    ;

    private final static ThreadLocal<ProcessingEnvironment> ENVIRONMENT_THREAD_LOCAL = new ThreadLocal<>();

    private static ProcessingEnvironment get() { return ENVIRONMENT_THREAD_LOCAL.get(); }

    static Messager getMessager() { return get().getMessager(); }

    public static void initialize(ProcessingEnvironment env) {
        ENVIRONMENT_THREAD_LOCAL.set(env);
        Environment2.initialize(env);
    }

    public static Elements getUtils() { return get().getElementUtils(); }

    public static Types getTypes() { return get().getTypeUtils(); }

    public static Filer getFiler() { return get().getFiler(); }

    public static void release() { ENVIRONMENT_THREAD_LOCAL.remove(); }
}
