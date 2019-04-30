package com.moon.core.util.runner;

import com.moon.core.util.require.Requires;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class RunnerSettingsTestTest {

    static final Requires REQUIRES = Requires.of();

    String str, name;

    @Test
    void testCheckName() {
        name = "aaa";
        REQUIRES.requireEquals(RunnerSettings.checkName(name), name);

        name = " aaa";
        REQUIRES.requireThrows(() -> RunnerSettings.checkName(name));
        name = "$_aaa";
        REQUIRES.requireEquals(RunnerSettings.checkName(name), name);
    }

    @Test
    void testName1() {
        name = "a aa";
        REQUIRES.requireThrows(() -> RunnerSettings.checkName(name));
    }

    @Test
    void testToNsName() {
        name = RunnerSettings.toNsName("str", "indexOf");

        REQUIRES.requireEquals("str.indexOf", name);
    }
}