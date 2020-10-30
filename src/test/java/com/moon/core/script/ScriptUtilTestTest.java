package com.moon.core.script;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author moonsky
 */
class ScriptUtilTestTest {

    Object res;

    @Test
    void testRunJSCode() {
        assertEquals(res, 2);
    }
}