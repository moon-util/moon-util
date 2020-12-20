package com.moon.data.jdbc.processing;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author benshaoye
 */
abstract class EnvUtils {

    private static ProcessingEnvironment ENV;

    private EnvUtils() {}

    public static void initialize(ProcessingEnvironment env) { ENV = env; }

    public static Elements getUtils() { return ENV.getElementUtils(); }

    public static Types getTypes() { return ENV.getTypeUtils(); }

    public static Filer getFiler() { return ENV.getFiler(); }

    public static Messager getMessager() { return ENV.getMessager(); }
}
