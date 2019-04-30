package com.moon.core.lang.ref;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 * @date 2018/9/16
 */
public class BooleanAccessorTest {

    @Test
    void testFlip() {
        assertTrue(BooleanAccessor.ofFalse().isFalse());
        assertFalse(BooleanAccessor.ofFalse().isTrue());

        assertTrue(BooleanAccessor.ofFalse().flip().isTrue());
    }

    @Test
    void testIfTrue() {
        String value = BooleanAccessor.ofTrue().ifFalseOrNull(() -> "name");
        assertNull(value);

        value = BooleanAccessor.ofTrue().ifTrueOrNull(() -> "name");
        assertNotNull(value);
        assertEquals(value, "name");

        try {
            InetAddress address = InetAddress.getLocalHost();
            InetAddress[] addresses = InetAddress.getAllByName("www.baidu.com");
            System.out.println(address);
            for (InetAddress local : addresses) {
                System.out.println(local);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
