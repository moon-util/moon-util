package com.moon.core.enums;

import com.moon.core.util.RandomUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
public class IntTestersTest {

    public IntTestersTest() {
    }

    @Test
    void testValueOf() throws Exception {
        for (int i = 0; i <= 1500; i++) {
            int value = RandomUtil.nextInt();
            Assertions.assertEquals(is(value), isEvenNumber(value));
            Assertions.assertEquals(is(value), isOdd(value));
        }
    }

    private static boolean is(int n){
        return n % 2 == 0;
    }

    private static boolean isEvenNumber(int n) {
        return (((n >> 1) << 1) == n);
    }

    private static boolean isOdd(int n) {
        return (n & 1) == 0;
    }
}
