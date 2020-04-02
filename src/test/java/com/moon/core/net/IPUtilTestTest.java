package com.moon.core.net;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class IPUtilTestTest {

    @Test
    void testGetLocalIPAddress() {
        System.out.println(IPUtil.getLocalIPAddress());
        System.out.println(IPUtil.getLocalIP6Address());
    }
}