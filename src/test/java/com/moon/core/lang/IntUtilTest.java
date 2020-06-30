package com.moon.core.lang;

import com.moon.core.util.RandomUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
            Assertions.assertEquals(ret, val);
        }
        for (int i = 0; i < 50; i++) {
            long val = RandomUtil.nextLong(10, Integer.MAX_VALUE);
            String str = LongUtil.toString(val, radix);
            long ret = LongUtil.parseLong(str, radix);
            Assertions.assertEquals(ret, val);
        }
    }
}
