package com.moon.core.script;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author benshaoye
 */
class ScriptUtilTestTest {
    Object res;

    @Test
    void testRunJSCode() {
        res = ScriptUtil.runJSCode("1+1");
        assertEquals(res, 2);
    }
}