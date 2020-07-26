package com.moon.core.lang;

import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class RuntimeUtilTestTest {

    @Test
    void testGetRuntime() throws Exception {
        System.out.println(Thread.currentThread().getId());
        System.out.println(ManagementFactory.getRuntimeMXBean().getName());
    }
}