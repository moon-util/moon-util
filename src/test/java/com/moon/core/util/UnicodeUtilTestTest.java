package com.moon.core.util;

import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author moonsky
 */
class UnicodeUtilTestTest {

    String str, str0, str1, str2, u, u0, u1, u2, u3;

    @Test
    void testIsUnicode() {
        str = "123";
        assertTrue(UnicodeUtil.isUnicode(str));
        u = UnicodeUtil.toSimpleUnicode(str);
        u0 = UnicodeUtil.toFullUnicode(str);

        System.out.println(u);
        System.out.println(u0);
    }

    @Test
    void testIsNotUnicode() {
        str = "1本少爷";

        u = UnicodeUtil.toFullUnicode(str);
        System.out.println(u);
        System.out.println(UnicodeUtil.isUnicode(u));

        u = UnicodeUtil.toSimpleUnicode(str);
        System.out.println(u);
        System.out.println(UnicodeUtil.isUnicode(u));
        System.out.println(UnicodeUtil.isSimpleUnicode(u));
    }

    @Test
    void testToString() {
        u = "123";
        System.out.println(UnicodeUtil.isSimpleUnicode(u));
    }

    @Test
    void testToSimpleUnicode() {
    }

    @Test
    void testToFullUnicode() {
    }
}