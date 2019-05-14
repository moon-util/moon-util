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
        LoggerUtil.setDefaultOnlyOnce(LoggerUtil.LoggerType.SLF4J);
        assertNotThrow(() -> LoggerUtil.setDefaultOnlyOnce(LoggerUtil.LoggerType.SLF4J));
        assertThrow(() -> LoggerUtil.slf4j());
        assertThrow(() -> LoggerUtil.log4j());
        assertThrow(() -> LoggerUtil.log4j2());
    }
}