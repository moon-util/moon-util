package com.moon.core.util.env;

import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class EnvUtilTestTest {

    @Test
    void testIsProduction() {
        Runtime runtime = Runtime.getRuntime();
        System.out.println(runtime.freeMemory());
        System.out.println(runtime.maxMemory());
        System.out.println(runtime.totalMemory());
        System.out.println(runtime.availableProcessors());
        System.out.println(System.getProperty("moon.production"));

        EnvUtil.ifProd(() -> System.out.println("====================="));
        EnvUtil.ifDev(() -> System.out.println("---------------"));
    }
}