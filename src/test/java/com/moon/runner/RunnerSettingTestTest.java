package com.moon.runner;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author moonsky
 */
class RunnerSettingTestTest {

    @Test
    void testName() throws Exception {
        ClassWriter writer = new ClassWriter(0);
    }

    String str, name;

    @Test
    void testCheckName() {
        name = "aaa";
        assertEquals(RunnerSetting.checkName(name), name);

        name = " aaa";
        assertThrows(Throwable.class, () -> RunnerSetting.checkName(name));
        name = "$_aaa";
        assertEquals(RunnerSetting.checkName(name), name);
    }

    @Test
    void testName1() {
        name = "a aa";
        assertThrows(Throwable.class, () -> RunnerSetting.checkName(name));
    }

    @Test
    void testToNsName() {
        name = RunnerSetting.toNsName("str", "indexOf");

        assertEquals("str.indexOf", name);
    }
}