package com.moon.core.util.logger;

import com.moon.core.AbstractTest;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class LoggerUtilTestTest extends AbstractTest {

    @Test
    void testSetDefaultOnlyOnce() {
        LoggerUtil.setDefaultOnlyOnce(LoggerUtil.LoggerType.SLF4J);
        assertThrow(() -> LoggerUtil.setDefaultOnlyOnce(LoggerUtil.LoggerType.SLF4J));
        assertThrow(() -> LoggerUtil.getSlf4j());
        assertThrow(() -> LoggerUtil.getLog4j());
        assertThrow(() -> LoggerUtil.getLog4j2());
    }
}