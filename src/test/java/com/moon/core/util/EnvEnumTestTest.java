package com.moon.core.util;

import com.moon.core.util.env.EnvUtil;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class EnvEnumTestTest {

    @Test
    void testGetModeOrNull() {
        String str = EnvUtil.current().toString();
        System.out.println(str);
    }

    @Test
    void testIsProduction() {
    }

    @Test
    void testIsDevelopment() {
    }
}