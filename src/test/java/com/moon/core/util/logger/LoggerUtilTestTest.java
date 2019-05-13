package com.moon.core.util.logger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author benshaoye
 */
class LoggerUtilTestTest {

    @Test
    void testSetDefaultOnlyOnce() {
        assertDoesNotThrow(() -> {
            LoggerUtil.setDefaultOnlyOnce(LoggerUtil.LoggerType.LOG4J);
        });
        assertThrows(Throwable.class, () -> {
            LoggerUtil.setDefaultOnlyOnce(LoggerUtil.LoggerType.SLF4J);
        });
    }
}