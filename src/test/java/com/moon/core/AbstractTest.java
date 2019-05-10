package com.moon.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

/**
 * @author benshaoye
 */
public class AbstractTest {

    public static void assertThrow(Executable executable) {
        Assertions.assertThrows(Throwable.class, executable);
    }
}
