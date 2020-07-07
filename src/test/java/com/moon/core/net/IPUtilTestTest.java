package com.moon.core.net;

import com.moon.core.enums.DateFormats;
import com.moon.core.enums.Patterns;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class IPUtilTestTest {

    @Test
    void testGetLocalIPAddress() {
        System.out.println(IPUtil.getLocalIPV4());
        System.out.println(IPUtil.getLocalIPV6Address());
        System.out.println(Patterns.IPV4.test(IPUtil.getLocalIPV6Address()));
        System.out.println(Patterns.IPV6.test(IPUtil.getLocalIPV6Address()));
    }
}