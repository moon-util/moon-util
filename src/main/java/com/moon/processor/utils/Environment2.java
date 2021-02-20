package com.moon.processor.utils;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author benshaoye
 */
public enum Environment2 {
    ;

    private final static ThreadLocal<ProcessingEnvironment> ENV_THREAD_LOCAL = new ThreadLocal<>();

    private static ProcessingEnvironment get() { return ENV_THREAD_LOCAL.get(); }

    public static void initialize(ProcessingEnvironment env) { ENV_THREAD_LOCAL.set(env); }

    public static Elements getUtils() { return get().getElementUtils(); }

    public static Types getTypes() { return get().getTypeUtils(); }

    public static Filer getFiler() { return get().getFiler(); }

    public static void release() { ENV_THREAD_LOCAL.remove(); }
}
