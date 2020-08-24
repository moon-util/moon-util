package com.moon.core.net;

import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
public class IPUtilTest {

    public IPUtilTest() {
    }

    @Test
    void testIpv4ToIpv6() throws Exception {
        String ipv4 = "135.75.43.52";
        String ipv6 = IPUtil.ipv4ToIpv6(ipv4);
        System.out.println(ipv6);
    }
}
