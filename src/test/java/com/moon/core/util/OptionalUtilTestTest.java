package com.moon.core.util;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

/**
 * @author moonsky
 */
class OptionalUtilTestTest {

    @Test
    void testApplyOrNull() {
        int count = 100;
        StringBuilder sb = new StringBuilder(count + 1);
        sb.append(1);
        for (int i = 0; i < count; i++) {
            sb.append(0);
        }
        BigInteger number = new BigInteger(sb.toString());
        number = number.add(number);
        System.out.println(number);
    }

    @Test
    void testApplyOrElse() {

    }

    @Test
    void testApplyOrGet() {
    }

    @Test
    void testIfPresent() {
    }

    @Test
    void testIfPresentOrElse() {
    }
}