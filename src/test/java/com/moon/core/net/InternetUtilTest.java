package com.moon.core.net;

import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
public class InternetUtilTest {
    @Test
    void testGetLocalIP4() {
        System.out.println(InternetUtil.getLocalIP4());
    }
}
