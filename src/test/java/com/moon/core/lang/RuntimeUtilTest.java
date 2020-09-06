package com.moon.core.lang;

import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;

/**
 * @author moonsky
 */
public class RuntimeUtilTest {

    public RuntimeUtilTest() {
    }

    @Test
    void testGetCurrentPID() throws Exception {
        System.out.println(ManagementFactory.getRuntimeMXBean().getName());
        System.out.println(RuntimeUtil.getCurrentPID());
    }
}
