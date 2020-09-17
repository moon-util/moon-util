package com.moon.core.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author moonsky
 */
class FastTimestampTestTest {

    @Test
    void testGetTimestamp() throws Exception {
        List<Long> times = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            times.add(FastTimestamp.currentTimeMillis());
        }
        System.out.println(times);
    }
}