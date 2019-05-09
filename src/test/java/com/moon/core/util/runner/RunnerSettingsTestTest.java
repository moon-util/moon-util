package com.moon.core.util.runner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author benshaoye
 */
class RunnerSettingsTestTest {


    String str, name;

    @Test
    void testCheckName() {
        name = "aaa";
        assertEquals(RunnerSettings.checkName(name), name);

        name = " aaa";
        assertThrows(Throwable.class, () -> RunnerSettings.checkName(name));
        name = "$_aaa";
        assertEquals(RunnerSettings.checkName(name), name);
    }

    @Test
    void testName1() {
        name = "a aa";
        assertThrows(Throwable.class, () -> RunnerSettings.checkName(name));
    }

    @Test
    void testToNsName() {
        name = RunnerSettings.toNsName("str", "indexOf");

        assertEquals("str.indexOf", name);
    }
}