package com.moon.core.net;

import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
public class InternetUtilTest {
    @Test
    void testGetLocalIP4() {
        System.out.println(NetworkUtil.getLocalIP4());
    }
}
