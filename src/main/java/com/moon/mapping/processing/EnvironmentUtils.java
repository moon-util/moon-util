package com.moon.mapping.processing;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author moonsky
 */
final class EnvironmentUtils {

    private static ProcessingEnvironment ENV;

    private EnvironmentUtils() { }

    static void initialize(ProcessingEnvironment environment) { ENV = environment; }

    public static ProcessingEnvironment getEnv() { return ENV; }

    public static Types getTypeUtils() { return getEnv().getTypeUtils(); }

    public static Elements getElementUtils() { return getEnv().getElementUtils(); }
}
