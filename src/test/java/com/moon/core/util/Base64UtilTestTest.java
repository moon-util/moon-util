package com.moon.core.util;

import com.moon.core.lang.StringUtil;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author benshaoye
 */
class Base64UtilTestTest {

    void doEncodeAndShow(String str) {
        byte[] bytes = str.getBytes();
        String encoded0 = Base64.getEncoder().encodeToString(bytes);
        String encoded1 = Base64Util.getEncoder().encodeToString(bytes);
        assertEquals(encoded0, encoded1);

        byte[] decoded0 = Base64.getDecoder().decode(encoded0);
        byte[] decoded1 = Base64Util.getDecoder().decode(encoded0);

        assertArrayEquals(decoded0, decoded1);
        assertEquals(str, new String(decoded0));

        String origin = StringUtil.padEnd(str, 35, ' ') + ": ";
        System.out.println(origin + encoded0);
    }

    @Test
    void testGetEncoder() {
        String a = "1";
        doEncodeAndShow(a);
        for (int i = 0; i < 10; i++) {
            doEncodeAndShow(RandomStringUtil.nextLetter(10, 30));
        }
    }
}