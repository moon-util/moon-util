package com.moon.core.lang;

import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
public class SystemUtilTest {

    public SystemUtilTest() {
    }

    @Test
    void testGetJavaVersion() throws Exception {
        System.out.println(SystemUtil.getJavaVersion());
    }

    @Test
    void testGetJavaVersionAsInt() throws Exception {
        System.out.println(SystemUtil.getJavaVersionAsInt());
    }
}
