package com.moon.core.lang;

import com.moon.core.util.RandomUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author moonsky
 */
public class IntUtilTest {

    public IntUtilTest() {
    }

    @Test
    void testParseInt() throws Exception {
        int radix = RandomUtil.nextInt(2, 62);
        System.out.println(radix);
        for (int i = 0; i < 50; i++) {
            int val = RandomUtil.nextInt(10, Integer.MAX_VALUE);
            String str = IntUtil.toString(val, radix);
            int ret = IntUtil.parseInt(str, radix);
            assertEquals(ret, val);
        }
        for (int i = 0; i < 50; i++) {
            long val = RandomUtil.nextLong(10, Integer.MAX_VALUE);
            String str = LongUtil.toString(val, radix);
            long ret = LongUtil.parseLong(str, radix);
            assertEquals(ret, val);
        }
    }

    @Test
    void testToUnusualString() throws Exception {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 100; i++) {
            int value = random.nextInt();
            String str = IntUtil.toCompressionString(value, IntUtil.MAX_RADIX);
            int parsed = IntUtil.parseCompressionString(str, IntUtil.MAX_RADIX);
            assertEquals(parsed, value);
        }

        for (int i = 0; i < 100; i++) {
            long value = random.nextLong();
            String str = LongUtil.toCompressionString(value, IntUtil.MAX_RADIX);
            long parsed = LongUtil.parseCompressionString(str, IntUtil.MAX_RADIX);
            assertEquals(parsed, value);
        }
        String str0 = LongUtil.toCompressionString(Long.MAX_VALUE - 1, Integer.MAX_VALUE);
        String str1 = LongUtil.toCompressionString(Long.MAX_VALUE, Integer.MAX_VALUE);
        String str2 = LongUtil.toCompressionString(Long.MAX_VALUE + 1, Integer.MAX_VALUE);
        System.out.println(str0);
        System.out.println(str1 + "\t" + String.valueOf(Long.MAX_VALUE).length() + "\t" + Long.MAX_VALUE);
        System.out.println(str2);
        System.out.println(str0.compareTo(str1) < 0);
        System.out.println(str1.compareTo(str2) > 0);
        assertTrue(str0.compareTo(str1) < 0);
        assertTrue(str1.compareTo(str2) > 0);
    }

    @Test
    @Disabled
    void testToUnusualString0() throws Exception {
        for (int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i++) {
            String str = IntUtil.toCompressionString(i, IntUtil.MAX_RADIX);
            int parsed = IntUtil.parseCompressionString(str, IntUtil.MAX_RADIX);
            assertEquals(parsed, i);
            if (i % 100000000 == 0) {
                System.out.println(i + "\t" + str);
            }
        }
    }

    @Test
    void testParseUnusualString() throws Exception {
    }
}
