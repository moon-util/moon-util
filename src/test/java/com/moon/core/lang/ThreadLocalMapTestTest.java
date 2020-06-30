package com.moon.core.lang;

import com.moon.core.util.ThreadLocalMap;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class ThreadLocalMapTestTest {

    public static final Map<Object, Object> threadLocalMap = new ThreadLocalMap<>();
    public static final ExecutorService executor = Executors.newFixedThreadPool(5);
    public static final ThreadLocalRandom random = ThreadLocalRandom.current();

    public static final Map<Thread, Object> seed = new ConcurrentHashMap<>();

    @Test
    void testSize() {
        for (int j = 0; j < 25; j++) {
            executor.submit(() -> {
                Thread thread = Thread.currentThread();
                if (!seed.containsKey(thread)) {
                    seed.put(thread, thread);
                    random.setSeed(new Random().nextLong());
                }

                int size = random.nextInt(1000);
                for (int i = 0; i < size; i++) {
                    threadLocalMap.put(random.nextLong(), random.nextLong());
                }
                int sizeOfMap = threadLocalMap.size();
                assertEquals(sizeOfMap, size);
            });
        }
    }

    @Test
    void testIsEmpty() {
    }

    @Test
    void testContainsKey() {
    }

    @Test
    void testContainsValue() {
    }

    @Test
    void testGet() {
    }

    @Test
    void testPut() {
    }

    @Test
    void testRemove() {
    }

    @Test
    void testPutAll() {
    }

    @Test
    void testClear() {
    }

    @Test
    void testKeySet() {
    }

    @Test
    void testValues() {
    }

    @Test
    void testEntrySet() {
    }

    @Test
    void testForEach() {
    }

    @Test
    void testMerge() {
    }

    @Test
    void testCompute() {
    }

    @Test
    void testComputeIfAbsent() {
    }

    @Test
    void testComputeIfPresent() {
    }

    @Test
    void testGetOrDefault() {
    }

    @Test
    void testReplaceAll() {
    }

    @Test
    void testPutIfAbsent() {
    }

    @Test
    void testRemove1() {
    }

    @Test
    void testReplace() {
    }

    @Test
    void testReplace1() {
    }

    @Test
    void testGet1() {
    }

    @Test
    void testRemove2() {
    }
}