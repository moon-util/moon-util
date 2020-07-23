package com.moon.core.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class FastTimestampTestTest {

    @Test
    void testGetTimestamp() throws Exception {
        FastTimestamp timestamp = FastTimestamp.getThreadInstance();
        ExecutorService service = new ThreadPoolExecutor(12, 12, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50));

        for (int i = 0; i < 20; i++) {
            service.submit(() -> {
                while (true) {
                    long timer = timestamp.getTimestamp();
                    System.out.println(timer);
                }
            });
        }
        while (true) {
        }
    }
}