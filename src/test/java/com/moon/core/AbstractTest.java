package com.moon.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

/**
 * @author moonsky
 */
public class AbstractTest {

    public static void assertNotThrow(Executable executable) {
        Assertions.assertDoesNotThrow(executable);
    }

    public static void assertThrow(Executable executable) {
        Assertions.assertThrows(Throwable.class, executable);
    }

    protected void runningTime(int count, Runnable runnable) {
        runningTime(count, runnable, null);
    }

    protected void runningTime(int count, Runnable runnable, String name) {
        final long time0 = System.currentTimeMillis();
        running(count, runnable);
        final long time1 = System.currentTimeMillis();
        if (name == null) {
            System.out.println(time1 - time0);
        } else {
            System.out.println(name + (time1 - time0));
        }
    }

    protected void running(int count, String name0, Runnable runnable0, String name1, Runnable runnable1) {
        runningTime(count, runnable0, name0);
        runningTime(count, runnable1, name1);
    }

    protected void running(int count, Runnable runnable) {
        for (int i = 0; i < count; i++) {
            runnable.run();
        }
    }
}
