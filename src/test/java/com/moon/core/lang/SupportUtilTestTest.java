package com.moon.core.lang;

import com.moon.core.lang.support.StringSupport;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class SupportUtilTestTest {

    @Test
    void testSetString() {
        String str = "benshaoye";
        char[] chars = StringSupport.setString(null, 0, str);
        String now = new String(chars, 0, str.length());

        assertFalse(Arrays.equals(chars, str.toCharArray()));
        assertTrue(now.equals(str));

        chars = StringSupport.setString(chars, chars.length, str);
        assertFalse(Arrays.equals(chars, (str + str).toCharArray()));
    }
}